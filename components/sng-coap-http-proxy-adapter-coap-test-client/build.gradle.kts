// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

dependencies {
    implementation("org.springframework:spring-context")

	// Californium CoAP framework}
	implementation("org.eclipse.californium:californium-core:${rootProject.extra["californiumVersion"]}")
	implementation("org.eclipse.californium:scandium:${rootProject.extra["californiumVersion"]}")
}
