/*
 * Copyright 2022 KriolOS.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.openbravo.pos.generated;

import java.io.Serializable;

/**
 * Version Template file
 *
 * Properties are generate at build time
 *
 * @author pauloborges
 */
public class Version implements Serializable {

    /**
     * The full version number of this release. Example "1.2.3".
     */
    private static final String VERSION_FULL;
    /**
     * Major version number. Example 1 in 1.2.3.
     */
    private static final int VERSION_MAJOR;

    /**
     * Minor version number. Example 2 in 1.2.3.
     */
    private static final int VERSION_MINOR;

    /**
     * Version revision number. Example 3 in 1.2.3.
     */
    private static final int VERSION_REVISION;

    /**
     * Build identifier. Example "SNAPSHOT" in 1.2.3.
     */
    private static final String VERSION_BUILD;

    /**
     * Build time
     */
    private static final String BUILD_TIMESTAMP;

    /**
     * BUILD SCM Revision Number
     */
    private static final String BUILD_NUMBER;

    /**
     * BUILD SCM Branch
     */
    private static final String BUILD_BRANCH;
    private static final long serialVersionUID = 1L;

    /* INIT */
    static {
        if ("${project.version}".equals("${" + "project.version" + "}")) {
            VERSION_FULL = "0.0.0-BUILD";
        } else {
            VERSION_FULL = "${project.version}";
        }
        final String[] digits = VERSION_FULL.split("[-.]", 4);
        String[] revisionParts = {};
        int major = -1;
        int minor = -1;
        int revision = 0;
        String build = "";

        if (isNumeric(digits[0])) {
            major = Integer.parseInt(digits[0]);
        }

        if (isNumeric(digits[1])) {
            minor = Integer.parseInt(digits[1]);
        }

        if (isNumeric(digits[2])) {
            revision = Integer.parseInt(digits[2]);
        } else {
            //WHEN format is "Y.Y.Y-SNAPSHOT"
            if (digits.length >= 2) {
                revisionParts = digits[2].split("\\-");
                if (revisionParts.length >= 1 && isNumeric(revisionParts[0])) {
                    revision = Integer.parseInt(revisionParts[0]);
                } else {
                    revision = 0;
                }
            }
        }

        if (digits.length == 4) {
            build = digits[3];
        }
        if (revisionParts.length == 2) {
            //WHEN format is "Y.Y.Y-SNAPSHOT"
            build = revisionParts[1];
        }

        VERSION_MAJOR = major;
        VERSION_MINOR = minor;
        VERSION_REVISION = revision;
        VERSION_BUILD = build;

        BUILD_TIMESTAMP = "${artifact.build.timestamp}";
        BUILD_NUMBER = "${artifact.build.scmnumber}";
        BUILD_BRANCH = "${artifact.build.scmbranch}";
    }

    public static boolean isNumeric(String str) {
        boolean idDouble;
        try {
            double number = Double.parseDouble(str);
            idDouble = (number != Double.MIN_VALUE);
        } catch (NumberFormatException nfe) {
            idDouble = false;
        }
        return idDouble;
    }

    public static String getFullVersion() {
        return VERSION_FULL;
    }

    public static String getFullVersionNumber() {
        return VERSION_MAJOR + "." + VERSION_MINOR + "." + VERSION_REVISION;
    }

    public static int getMajorVersion() {
        return VERSION_MAJOR;
    }

    public static int getMinorVersion() {
        return VERSION_MINOR;
    }

    public static int getRevision() {
        return VERSION_REVISION;
    }

    public static String getBuildIdentifier() {
        return VERSION_BUILD;
    }

    public static String getBuildTimestamp() {
        return BUILD_TIMESTAMP;
    }

    public static String getBuildNumber() {
        return BUILD_NUMBER;
    }

    public static String getBuildScmBranch() {
        return BUILD_BRANCH;
    }
}
