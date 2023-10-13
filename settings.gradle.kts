// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

rootProject.name = "sng-coap-http-proxy"

include("components:sng-coap-http-proxy-domain")
include("components:sng-coap-http-proxy-application")
include("components:sng-coap-http-proxy-adapter-coap-server")
include("components:sng-coap-http-proxy-adapter-coap-test-client")
include("components:sng-coap-http-proxy-adapter-http-client")
include("components:sng-coap-http-proxy-adapter-psk-stub")
include("application")

