package com.example.violations.os;
import com.sun.jna.platform.win32.Advapi32;
public class WindowsApiUsage { void run(){ Advapi32.INSTANCE.GetUserName(null,null); } }