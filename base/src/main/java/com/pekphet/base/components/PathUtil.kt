// Copyright (c) 2019-present Pekphet.  All rights reserved
// Confidential and Proprietary
package com.pekphet.base.components

class PathUtil {
    fun getFileName(fullPath:String) : String{
        return if(fullPath.contains("/")) {
            val start = fullPath.lastIndexOf("/")
            fullPath.substring(start + 1)
        }else{
            fullPath
        }
    }
    fun getPath(fullPath:String) : String? {
        return if(fullPath.contains("/")) {
            val start = fullPath.lastIndexOf("/")
            fullPath.substring(0,start)
        }else{
            ""
        }
    }
    fun getFileNameWithoutExtension(fullPath: String):String{
        val fileName = getFileName(fullPath)
        return if(fileName.contains(".")) {
            val start = fullPath.lastIndexOf(".")
            fullPath.substring(0,start)
        }else{
            fileName
        }
    }

}