package com.anvesh.nogozoshopapplication.datamodels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ItemGroup {
    @SerializedName("group_id")
    @Expose
    lateinit var groupId: String

    @SerializedName("group_name")
    @Expose
    lateinit var groupName: String

    constructor(groupId: String, groupName: String){
        this.groupId = groupId
        this.groupName = groupName
    }

    constructor()
}