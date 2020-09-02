package com.anvesh.nogozoshopapplication.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.anvesh.nogozoshopapplication.R
import com.anvesh.nogozoshopapplication.SessionManager
import com.anvesh.nogozoshopapplication.datamodels.VendorProfile
import com.anvesh.nogozoshopapplication.ui.BaseActivity
import com.anvesh.nogozoshopapplication.ui.contactus.ContactUsFaqActivity
import com.anvesh.nogozoshopapplication.ui.inventory.VendorInventoryActivity
import com.anvesh.nogozoshopapplication.ui.main.status.CurrentStatusFragment
import com.anvesh.nogozoshopapplication.ui.main.vendor.orders.current.VendorCurrentOrdersFragment
import com.anvesh.nogozoshopapplication.ui.orders.OrdersActivity
import com.anvesh.nogozoshopapplication.ui.orders.vendor.past.VendorPastOrdersFragment
import com.anvesh.nogozoshopapplication.ui.profile.ProfileActivity
import com.anvesh.nogozoshopapplication.ui.splash.SplashActivity
import com.anvesh.nogozoshopapplication.ui.stats.StatsActivity
import com.anvesh.nogozoshopapplication.util.Constants.USER_TYPE
import com.anvesh.nogozoshopapplication.util.Constants.userType_VENDOR
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem

class MainActivity : BaseActivity(), Communicator, View.OnClickListener {

    //@Inject
    lateinit var sessionManager: SessionManager


    private lateinit var drawerButton: ImageButton
    private lateinit var drawer: Drawer

    lateinit var shopNameHeader: TextView
    lateinit var shopPhoneHeader: TextView
    lateinit var shopAddressHeader: TextView
    private var vendorProfile: VendorProfile? = null

    lateinit var viewPager: ViewPager
    lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sessionManager = SessionManager()

        drawerButton = findViewById(R.id.header_profile)
        drawerButton.setOnClickListener(this)

        viewPager = findViewById(R.id.main_container)
        tabLayout = findViewById(R.id.tabLayout)

        val userType = intent.getStringExtra(USER_TYPE)
        if (userType == userType_VENDOR) {
            buildVendorDrawer()
            getVendorProfile()
            startFragment(CurrentStatusFragment())
            setUpViewPageAdapter()
        }
    }

    private fun setUpViewPageAdapter() {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(VendorCurrentOrdersFragment(),"Pending Orders")
        adapter.addFragment(VendorPastOrdersFragment(), "Completed Orders")
        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)
    }

    private fun getVendorProfile() {
        val ref = FirebaseDatabase.getInstance().getReference("/users/$userType_VENDOR/${FirebaseAuth.getInstance().currentUser?.uid}/profile")

        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.value != null){
                    vendorProfile = snapshot.getValue(VendorProfile::class.java)
                    setUpVendorHeaderViews()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun setUpVendorHeaderViews(){
        shopNameHeader = drawer.header!!.findViewById(R.id.drawerHeaderShopName)
        shopPhoneHeader = drawer.header!!.findViewById(R.id.drawerHeaderPhoneNumber)
        shopAddressHeader = drawer.header!!.findViewById(R.id.drawerHeaderShopAddress)

        shopNameHeader.text = vendorProfile!!.shopname
        shopPhoneHeader.text = vendorProfile!!.phone
        shopAddressHeader.text = vendorProfile!!.address
    }

    private fun buildVendorDrawer() {
        drawer = DrawerBuilder()
            .withActivity(this)
            .withTranslucentStatusBar(true)
            .withDrawerGravity(GravityCompat.END).withHeader(R.layout.header_view_vendor_navigation_drawer)
            .addDrawerItems(
                PrimaryDrawerItem().withIdentifier(1).withName("Inventory").withSelectable(false)
                    .withTextColor(ContextCompat.getColor(this, R.color.colorPrimaryLight)).withIcon(R.drawable.ic_quantity),
                PrimaryDrawerItem().withIdentifier(2).withName("Past Orders").withSelectable(false)
                    .withTextColor(ContextCompat.getColor(this, R.color.colorPrimaryLight)).withIcon(R.drawable.ic_orders),
                PrimaryDrawerItem().withIdentifier(3).withName("Stats").withSelectable(false)
                    .withTextColor(ContextCompat.getColor(this, R.color.colorPrimaryLight)).withIcon(R.drawable.ic_stats),
                PrimaryDrawerItem().withIdentifier(4).withName("Profile").withSelectable(false)
                    .withTextColor(ContextCompat.getColor(this, R.color.colorPrimaryLight)).withIcon(R.drawable.ic_profile),
                PrimaryDrawerItem().withIdentifier(5).withName("Contact Us").withSelectable(false)
                    .withTextColor(ContextCompat.getColor(this,R.color.colorPrimaryLight)).withIcon(R.drawable.ic_contact_us),
                DividerDrawerItem().withIdentifier(6),
                SecondaryDrawerItem().withIdentifier(7).withName("Sign Out").withSelectable(false)
                    .withTextColor(ContextCompat.getColor(this, R.color.red)).withIcon(R.drawable.ic_sign_out)
            )
            .withOnDrawerItemClickListener(object : Drawer.OnDrawerItemClickListener {
                override fun onItemClick(
                    view: View?,
                    position: Int,
                    drawerItem: IDrawerItem<*>
                ): Boolean {
                    when (position) {
                        1 -> {
                            val i = Intent(this@MainActivity, VendorInventoryActivity::class.java)
                            startActivity(i)
                        }
                        2 -> {
                            val i = Intent(this@MainActivity, OrdersActivity::class.java)
                            i.putExtra(USER_TYPE, userType_VENDOR)
                            startActivity(i)
                        }
                        3 -> {
                            val i = Intent(this@MainActivity, StatsActivity::class.java)
                            i.putExtra(USER_TYPE, userType_VENDOR)
                            startActivity(i)
                        }
                        4 -> {
                            val i = Intent(this@MainActivity, ProfileActivity::class.java)
                            i.putExtra(USER_TYPE, userType_VENDOR)
                            startActivity(i)
                        }
                        5 -> {
                            val intent = Intent(this@MainActivity, ContactUsFaqActivity::class.java)
                            startActivity(intent)
                        }
                        7 -> {
                            sessionManager.logout()
                            val i = Intent(this@MainActivity, SplashActivity::class.java)
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            startActivity(i)
                        }
                    }
                    drawer.closeDrawer()
                    return true
                }
            })
            .build()
        drawer.setSelection(-1, false)
    }

    private fun startFragment(fragment: Fragment) {
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.frame_layout, fragment)
        ft.addToBackStack(fragment.tag)
        ft.commit()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 1)
            finish()
        else
            super.onBackPressed()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.header_profile -> {
                drawer.openDrawer()
            }
        }
    }
}