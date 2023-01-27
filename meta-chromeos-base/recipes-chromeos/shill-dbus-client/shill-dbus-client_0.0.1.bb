SUMMARY = "Shill DBus client interface library"
DESCRIPTION = "Shill DBus client interface library"
HOMEPAGE = "https://chromium.googlesource.com/chromiumos/platform2/+/HEAD/shill/dbus/client"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://${CHROMEOS_COMMON_LICENSE_DIR}/BSD-Google;md5=29eff1da2c106782397de85224e6e6bc"

inherit chromeos_gn platform

CHROMEOS_PN = "shill/dbus/client"

S = "${WORKDIR}/src/platform2/${CHROMEOS_PN}"
B = "${WORKDIR}/build"
PR = "r958"

DEPENDS += "shill-client shill-net"

GN_ARGS += 'platform_subdir="${CHROMEOS_PN}"'

PACKAGECONFIG ??= ""

PACKAGECONFIG[cros_host] = ""
PACKAGECONFIG[profiling] = ""
PACKAGECONFIG[tcmalloc] = ""
PACKAGECONFIG[test] = ""

GN_ARGS += ' \
    use={ \
        cros_host=${@bb.utils.contains('PACKAGECONFIG', 'cros_host', 'true', 'false', d)} \
        profiling=${@bb.utils.contains('PACKAGECONFIG', 'profiling', 'true', 'false', d)} \
        tcmalloc=${@bb.utils.contains('PACKAGECONFIG', 'tcmalloc', 'true', 'false', d)} \
        test=${@bb.utils.contains('PACKAGECONFIG', 'test', 'true', 'false', d)} \
    } \
'

export SO_VERSION="1"

do_install() {
    install -d "${D}${libdir}"
    install -m 0755 lib/libshill-dbus-client.so ${D}${libdir}/libshill-dbus-client.so.${SO_VERSION}
    ln -sf libshill-dbus-client.so.${SO_VERSION} ${D}${libdir}/libshill-dbus-client.so

    # Install libshill-dbus-client library.
    ( cd "${S}"; ./preinstall.sh "${B}" 1; )
    install -d "${D}${libdir}/pkgconfig"
    install -m 0644 "lib/libshill-dbus-client.pc" "${D}${libdir}/pkgconfig/"

    # Install header files from libshill-dbus-client
    install -d ${D}${includedir}/shill/dbus/client
    install -m 0644 ${S}/*.h ${D}${includedir}/shill/dbus/client/
}

