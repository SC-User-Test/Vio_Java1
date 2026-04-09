package com.example.violations.exec;
public class ShellCommandRunner{ void run() throws Exception{ Runtime.getRuntime().exec("cmd.exe /c dir"); } }