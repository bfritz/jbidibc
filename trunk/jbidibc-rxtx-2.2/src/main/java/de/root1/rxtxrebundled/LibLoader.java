/*--------------------------------------------------------------------------
|   rxtx-rebundled LibLoader is a helper class for rxtx library.
|   Copyright 2012 by Alexander Christian alex@root1.de
|
|   This library is free software; you can redistribute it and/or
|   modify it under the terms of the GNU Library General Public
|   License as published by the Free Software Foundation; either
|   version 2 of the License, or (at your option) any later version.
|
|   This library is distributed in the hope that it will be useful,
|   but WITHOUT ANY WARRANTY; without even the implied warranty of
|   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
|   Library General Public License for more details.
|
|   You should have received a copy of the GNU Library General Public
|   License along with this library; if not, write to the Free
|   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
--------------------------------------------------------------------------*/
package de.root1.rxtxrebundled;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper Class used by rxtx implementation to load native libs without having java.library.path property specified.
 * Native libs will be extracted and loaded automatically, if possible
 * 
 * @author achr
 */
public class LibLoader {

    private static final String LIBLOADER_VERSION = "2.2";

    private static final List<String> loadedLibs = new ArrayList<String>();

    private static void logStdOut(String msg) {
        String property = System.getProperty("rxtx.rebundled.debug", "false");
        boolean log = Boolean.parseBoolean(property);
        if (log) {
            System.out.println(msg);
        }
    }

    private static void logStdErr(String msg) {
        String property = System.getProperty("rxtx.rebundled.suppress_error", "false");
        boolean suppress = Boolean.parseBoolean(property);
        if (!suppress) {
            System.err.println(msg);
        }
    }

    private static void logExceptionToStdErr(Exception ex) {
        String property = System.getProperty("rxtx.rebundled.suppress_error", "false");
        boolean suppress = Boolean.parseBoolean(property);
        if (!suppress) {
            ex.printStackTrace();
        }
    }

    public static void loadLibrary(String name) {

        logStdOut("Trying to load '" + name + "' ...");
        if (loadedLibs.contains(name)) {
            logStdOut("Library is already loaded: '" + name + "' ...");
            return;
        }

        final String envLibPath = System.getProperty("rxtx.rebundled.libraryPath");
        final String libraryPath = envLibPath != null ? envLibPath : prepareLibraryPath();
        if (libraryPath == null) {
            logStdErr("The required library is not available for your system!");
            return;
        }

        logStdOut("Try to load library from path: " + libraryPath);
        try {
            if (envLibPath != null) {
                System.load(envLibPath);
            }
            else {
                NativeUtils.loadLibraryFromJar(libraryPath, LIBLOADER_VERSION); // during runtime. .DLL within .JAR
            }
        }
        catch (IOException e1) {
            logStdErr("Load native library failed: " + libraryPath);
            logExceptionToStdErr(e1);
            throw new RuntimeException(e1);
        }
        loadedLibs.add(name);

        logStdOut("...*done*");
    }

    private static String prepareLibraryPath() {
        String osName = System.getProperty("os.name");
        String osArch = System.getProperty("os.arch");

        logStdOut("os.name='" + osName + "'");
        logStdOut("os.arch='" + osArch + "'");

        String libraryPath = null;

        // check for linux platform ..
        if (osName.toLowerCase().contains("linux")) {

            // check for architecture 64bit
            if (osArch.toLowerCase().contains("amd64") || osArch.toLowerCase().contains("x86_64")) {
                libraryPath = "/rxtx/linux/x86_64/librxtxSerial64.so";
            }
            // arm
            else if (osArch.toLowerCase().equals("arm")) {
                libraryPath = "/rxtx/linux/arm/librxtxSerial.so";
            }
            // else 32bit
            else {
                libraryPath = "/rxtx/linux/i386/librxtxSerial.so";
            }

        }
        // check for windows platform
        else if (osName.toLowerCase().contains("windows")) {

            // check for architecture 64bit
            if (osArch.toLowerCase().contains("amd64") || osArch.toLowerCase().contains("x86_64")) {
                libraryPath = "/rxtx/windows/x86_64/rxtxSerial64.dll";
            }
            // else 32bit
            else {
                libraryPath = "/rxtx/windows/x86/rxtxSerial.dll";
            }

        }
        // check for os x platform
        else if (osName.toLowerCase().contains("os x")) {
            if (osArch.toLowerCase().contains("amd64") || osArch.toLowerCase().contains("x86_64")) {
                libraryPath = "/rxtx/mac/librxtxSerial64.jnilib";
            }
            else {
                libraryPath = "/rxtx/mac/librxtxSerial.jnilib";
            }
        }
        // other platforms are currently not supported ...
        else {
            logStdErr("Sorry, platform '"
                + osName
                + "' currently not supported by LibLoader. Please use -Djava.library.path=<insert path to native libs here> as JVM parameter...");
        }

        logStdOut("Prepared library path: " + libraryPath);

        return libraryPath;
    }
}
