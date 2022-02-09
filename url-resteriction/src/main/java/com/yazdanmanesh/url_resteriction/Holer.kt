package com.yazdanmanesh.url_resteriction

class Holer {
    companion object {
           var myRestrictedAddress:String = ""
           var REDIRECT_TO:String = ""

        fun setRestrictedAddress(myRestrictedAddress:String){
            this.myRestrictedAddress = myRestrictedAddress;
        }
        fun getRestrictedAddress() = myRestrictedAddress;

        fun setRedirectTo(REDIRECT_TO:String){
            this.REDIRECT_TO = REDIRECT_TO;
        }
        fun getRedirectTo() = REDIRECT_TO;



    }
}