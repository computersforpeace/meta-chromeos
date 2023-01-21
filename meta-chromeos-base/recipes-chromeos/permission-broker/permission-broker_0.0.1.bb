SUMMARY = "Permission Broker for Chromium OS"
DESCRIPTION = "Permission Broker for Chromium OS"
HOMEPAGE = "https://chromium.googlesource.com/chromiumos/platform2/+/HEAD/permission_broker/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://${CHROMEOS_COMMON_LICENSE_DIR}/BSD-Google;md5=29eff1da2c106782397de85224e6e6bc"

inherit chromeos_gn

CHROMEOS_PN = "permission_broker"

S = "${WORKDIR}/src/platform2/${CHROMEOS_PN}"
B = "${WORKDIR}/build"
PR = "r3275"

# dbusxx-xml2cpp is provided by libdbus-c++-native
# protoc is provided by protbuf-native
# protoc-gen-go is provided by github.com-golang-protobuf-native
# go-generate-chromeos-dbus-bindings is provided by chromeos-dbus-bindings-native
DEPENDS:append = "\
    chromeos-dbus-bindings-native \
    dbus \
    libbrillo \
    libchrome \
    patchpanel-client \
    udev \
    libusb \
    protobuf \
"

GN_ARGS += 'platform_subdir="${CHROMEOS_PN}"'

PACKAGECONFIG ??= ""

# Description of all the possible PACKAGECONFIG fields (comma delimited):
# 1. Extra arguments that should be added to the configure script argument list (EXTRA_OECONF or PACKAGECONFIG_CONFARGS) if the feature is enabled.
# 2. Extra arguments that should be added to EXTRA_OECONF or PACKAGECONFIG_CONFARGS if the feature is disabled.
# 3. Additional build dependencies (DEPENDS) that should be added if the feature is enabled.
# 4. Additional runtime dependencies (RDEPENDS) that should be added if the feature is enabled.
# 5. Additional runtime recommendations (RRECOMMENDS) that should be added if the feature is enabled.
# 6. Any conflicting (that is, mutually exclusive) PACKAGECONFIG settings for this feature.

# Empty PACKAGECONFIG options listed here to avoid warnings.
# The .bb file should use these to conditionally add patches,
# command-line switches and dependencies.
PACKAGECONFIG[cfm_enabled_device] = ""
PACKAGECONFIG[cros_host] = ""
PACKAGECONFIG[fuzzer] = ""
PACKAGECONFIG[profiling] = ""
PACKAGECONFIG[tcmalloc] = ""
PACKAGECONFIG[test] = ""

GN_ARGS += ' \
    use={ \
        cfm_enabled_device=${@bb.utils.contains('PACKAGECONFIG', 'cfm_enabled_device', 'true', 'false', d)} \
        cros_host=${@bb.utils.contains('PACKAGECONFIG', 'cros_host', 'true', 'false', d)} \
        fuzzer=${@bb.utils.contains('PACKAGECONFIG', 'fuzzer', 'true', 'false', d)} \
        profiling=${@bb.utils.contains('PACKAGECONFIG', 'profiling', 'true', 'false', d)} \
        tcmalloc=${@bb.utils.contains('PACKAGECONFIG', 'tcmalloc', 'true', 'false', d)} \
        test=${@bb.utils.contains('PACKAGECONFIG', 'test', 'true', 'false', d)} \
    } \
'

do_install() {
    install -d "${D}${bindir}"
    install -m 0755 permission_broker "${D}${bindir}/"

    install -d "${D}${sysconfdir}/init"
    install -m 0644 "${S}/permission_broker.conf" "${D}${sysconfdir}/init/"

    install -d "${D}${sysconfdir}/dbus-1/system.d"
    install -m 0644 "${S}/dbus/org.chromium.PermissionBroker.conf" "${D}${sysconfdir}/dbus-1/system.d/"

    # TODO: udev 99-hidraw.rules
    #install -d "${D}/lib/udev/rules.d"
    #install -m 0644 "${S}"
}

