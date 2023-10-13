// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

dependencies {
    implementation("org.springframework:spring-context")

    // Stand-alone notifying gateway
    api(project(":components:sng-coap-http-proxy-domain"))
    api(project(":components:sng-coap-http-proxy-application"))
}
