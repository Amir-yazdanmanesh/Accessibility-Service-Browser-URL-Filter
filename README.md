

[![](https://jitpack.io/v/Amir-yazdanmanesh/Accessibility-Service-Browser-URL-Filter.svg)](https://jitpack.io/#Amir-yazdanmanesh/Accessibility-Service-Browser-URL-Filter) [![android Status](https://img.shields.io/badge/platform-Android-yellow.svg)](https://www.android.com/)

## Filter url from browser by using accessibility service
In this repository, You can restrict the URLs that the user enters in their browser.

# Accessibility services
Accessibility services should only be used to assist users with disabilities in using Android devices and apps. They run in the background and receive callbacks by the system when AccessibilityEvents are fired. Such events denote some state transition in the user interface, for example, the focus has changed, a button has been clicked, etc.


- use [this link](https://developer.android.com/reference/android/accessibilityservice/AccessibilityService) to read more.

# Install
### Step 1 :
Add it in your root build.gradle at the end of repositories:

```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
### Step 2 :
Add the dependency


```
dependencies {
	        implementation 'com.github.Amir-yazdanmanesh:Accessibility-Service-Browser-URL-Filter:1.0.3'
	}

```


### Step 3 :
Active accessibility service in your device
``` 
startActivity(  
    Intent(  
        Settings.ACTION_ACCESSIBILITY_SETTINGS  
  ) 
   ``` 
### Step 4 :

Set  restricted address and redirect address
``` 
val myService = AccessibilityUtils.Builder()  
myService.setMyRestrictedAddress("www.facebook.com")  
myService.setRedirectTo("http://www.404.net")  
myService.build()
```

Have fun!  😉


The accessibility service will look for an id of an address bar text field and try to intercept the URL from there.
We detect either manual user input or redirect on link pressing (because in this case, the new URL will be also visible in an address bar).


## Requirements
- You must have to give background permission to activate the application on Xiaomi devices
- My filter URL on Android 5.0+ (API level 21+) .





![](https://raw.githubusercontent.com/Amir-yazdanmanesh/Accessibility-Service/master/screenshot/screenshot.gif) 
