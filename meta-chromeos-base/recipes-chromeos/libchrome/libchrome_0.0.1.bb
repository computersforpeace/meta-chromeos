SUMMARY = "Base library for Chromium OS"
DESCRIPTION="Chrome base/ and dbus/ libraries extracted for use on Chrome OS"
HOMEPAGE="http://dev.chromium.org/chromium-os/packages/libchrome"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://${CHROMEOS_COMMON_LICENSE_DIR}/BSD-Google;md5=29eff1da2c106782397de85224e6e6bc"

inherit chromeos_gn platform
require recipes-chromeos/chromiumos-platform/chromiumos-platform-${BPN}.inc

CHROMEOS_PN = "libchrome"

DEPENDS:append = " python3-native abseil-cpp glib-2.0 libevent gtest modp-b64 double-conversion re2"

S = "${WORKDIR}/src/platform2/libchrome"
B = "${WORKDIR}/build"
PR = "r410"

CC:append = " -I${STAGING_INCDIR}"
CPP:append = " -I${STAGING_INCDIR}"
CXX:append = " -I${STAGING_INCDIR}"
CXXFLAGS:append = " -Wno-error=implicit-int-float-conversion"

# Unused patches to port to OpenSSL 3.0
# file://0013-libchrome-crypto-secure_hash-port-to-EVP.patch
# file://0014-libchrome-crypto-scoped_openssl_types-drop-deprecated.patch
SRC_URI += " \
    gitsm://chromium.googlesource.com/linux-syscall-support;protocol=https;branch=main;destsuffix=src/platform2/libchrome/third_party/lss;name=lss \
    file://0001-libchrome-base-hash-md5.h-include-string.h.patch \
    file://0002-libchrome-base-metrics-histogram-double_t.patch \
    file://0003-libchrome-base-metrics-histogram-static_cast.patch \
    file://0004-libchrome-base-metrics-sample_vector-static_cast.patch \
    file://0005-libchrome-base-metrics-histogram-more-static_cast.patch \
    file://0006-libchrome-base-process-process_metrics-static_cast.patch \
    file://0007-libchrome-base-numerics-clamped_math_impl-cast.patch \
    file://0008-libchrome-base-time-time_delta_from_string-cast.patch \
    file://0009-libchrome-base-time-time-static_cast-double.patch \
    file://0010-libchrome-crypto-nss_util-use-nss3-nss.h-path.patch \
    file://0011-libchrome-crypto-p224_spoke-include-string.h.patch \
    file://0012-libchrome-crypto-scoped_nss_types-fix-nss.h-path.patch \
    file://0015-Mojo-Update-to-python3-shebang.patch \
"

SRCREV_lss = "92a65a8f5d705d1928874420c8d0d15bde8c89e5"

# The order of patches is set in src/platform/libchrome/libchrome_tools/patches/patches
#
# libchrome/libchrome_tools contains its own series of patches; manually apply
# them before applying other local patches.

do_patch[depends] += "quilt-native:do_populate_sysroot"

libchrome_do_patch() {
    cd ${S}
    # The patches are not numbered, so we need to use the order in the
    # patches file
    for f in $(grep "^[^#].*\.patch$" ${S}/libchrome_tools/patches/patches); do
        patch -p0 <${S}/libchrome_tools/patches/${f} || \
        patch -p1 <${S}/libchrome_tools/patches/${f}
    done

    # CrOS SDK currently ships a python==python3 to hide problems like this:
    sed -i 's:/usr/bin/env python$:/usr/bin/env python3:' "${S}"/mojo/public/tools/bindings/mojom_bindings_generator.py
}

do_unpack[cleandirs] += "${S}"

# We invoke base do_patch at end, to incorporate any local patches
python do_patch() {
    bb.build.exec_func('libchrome_do_patch', d)
    bb.build.exec_func('patch_do_patch', d)
}

PACKAGECONFIG ??= "crypto dbus mojo"

# Description of all the possible PACKAGECONFIG fields:
# 1. Extra arguments that should be added to the configure script argument list (EXTRA_OECONF or PACKAGECONFIG_CONFARGS) if the feature is enabled.
# 2. Extra arguments that should be added to EXTRA_OECONF or PACKAGECONFIG_CONFARGS if the feature is disabled.
# 3. Additional build dependencies (DEPENDS) that should be added if the feature is enabled.
# 4. Additional runtime dependencies (RDEPENDS) that should be added if the feature is enabled.
# 5. Additional runtime recommendations (RRECOMMENDS) that should be added if the feature is enabled.
# 6. Any conflicting (that is, mutually exclusive) PACKAGECONFIG settings for this feature.

PACKAGECONFIG[crypto] = ",,nss openssl,,,"
PACKAGECONFIG[dbus] = ",,dbus protobuf,,,"

# Empty PACKAGECONFIG options listed here to avoid warnings.
# The .bb file should use these to conditionally add patches
# and command-line switches (extra dependencies should not
# be necessary but are OK to add).
PACKAGECONFIG[asan] = ""
PACKAGECONFIG[board_use_mistral] = ""
PACKAGECONFIG[coverage] = ""
PACKAGECONFIG[cros_host] = ""
PACKAGECONFIG[fuzzer] = ""
PACKAGECONFIG[media_perception] = ""
PACKAGECONFIG[mojo] = ""
PACKAGECONFIG[msan] = ""
PACKAGECONFIG[profiling] = ""
PACKAGECONFIG[tcmalloc] = ""
PACKAGECONFIG[test] = ""
PACKAGECONFIG[ubsan] = ""


GN_ARGS += 'platform_subdir="${CHROMEOS_PN}"'

GN_ARGS += ' \
    use={ \
        asan=${@bb.utils.contains('PACKAGECONFIG', 'asan', 'true', 'false', d)} \
        board_use_mistral=${@bb.utils.contains('PACKAGECONFIG', 'board_use_mistral', 'true', 'false', d)} \
        coverage=${@bb.utils.contains('PACKAGECONFIG', 'coverage', 'true', 'false', d)} \
        cros_host=${@bb.utils.contains('PACKAGECONFIG', 'cros_host', 'true', 'false', d)} \
        crypto=${@bb.utils.contains('PACKAGECONFIG', 'crypto', 'true', 'false', d)} \
        dbus=${@bb.utils.contains('PACKAGECONFIG', 'dbus', 'true', 'false', d)} \
        fuzzer=${@bb.utils.contains('PACKAGECONFIG', 'fuzzer', 'true', 'false', d)} \
        media_perception=${@bb.utils.contains('PACKAGECONFIG', 'media_perception', 'true', 'false', d)} \
        mojo=${@bb.utils.contains('PACKAGECONFIG', 'mojo', 'true', 'false', d)} \
        msan=${@bb.utils.contains('PACKAGECONFIG', 'msan', 'true', 'false', d)} \
        profiling=${@bb.utils.contains('PACKAGECONFIG', 'profiling', 'true', 'false', d)} \
        tcmalloc=${@bb.utils.contains('PACKAGECONFIG', 'tcmalloc', 'true', 'false', d)} \
        test=${@bb.utils.contains('PACKAGECONFIG', 'test', 'true', 'false', d)} \
        ubsan=${@bb.utils.contains('PACKAGECONFIG', 'ubsan', 'true', 'false', d)} \
    } \
'

export SO_VERSION="1"

# Like portage recursive doins.
doins_r() {
    target_dir="$1"
    shift
    for s in "$@"; do
        if [ -f "${s}" ]; then
            install -D -m 0644 "${s}" "${target_dir}/$(basename "${s}")"
            continue
        fi

        find "$s" -depth -type f -printf '%P\0' | while read -d $'\0' f; do
            install -D -m 0644 "${s}/${f}" "${target_dir}/$(basename "${s}")/${f}"
        done
    done
}

other_install() {
    install -d ${D}${libdir}
    find "${B}${base_libdir}" -type f -name "lib*.so" -print0 | while read -d $'\0' f; do
        fn=$(basename ${f})
        echo ${fn}
        install -m 0755 ${f} ${D}${libdir}/${fn}.${SO_VERSION}
        ln -sf ${fn}.${SO_VERSION} ${D}${libdir}/${fn}
    done

    install -d ${D}${libdir}/pkgconfig
    find "${B}" -name "*.pc" -print0 | while read -d $'\0' pkgconfig; do
	install -m 0644 $pkgconfig ${D}${libdir}/pkgconfig/
    done

    INCLUDE_DIRS="base build components crypto dbus ipc mojo testing third_party"
    install -d ${D}${includedir}/libchrome
    for dir in ${INCLUDE_DIRS}; do
        find ${S}/${dir} -name *.h | sed -e "s,${S}/,,g" | while read f; do
            install -D -m 0644 ${S}/${f} ${D}${includedir}/libchrome/${f}
        done
    done

    # TODO: more libmojo files to install?
    if ${@bb.utils.contains('PACKAGECONFIG', 'mojo', 'true', 'false', d)}; then
        doins_r "${D}"/usr/src/libmojo/mojo \
            "${S}/mojo/public/tools/bindings"/* \
            "${S}/mojo/public/tools/mojom"/* \
            "${S}/build/gn_helpers.py" \
            "${S}/build/android/gyp/util" \
            "${S}/build/android/pylib"

        doins_r "${D}/usr/src/libmojo/third_party" \
            "${S}/third_party/jinja2" \
            "${S}/third_party/markupsafe" \
            "${S}/third_party/ply"

        for f in \
              mojo/public/tools/bindings/generate_type_mappings.py \
              mojo/public/tools/bindings/mojom_bindings_generator.py \
              mojo/public/tools/mojom/mojom_parser.py; do
            install -m 0755 "${S}/${f}" "${D}/usr/src/libmojo/mojo/"
        done

        for d in mojo/public/interfaces/bindings mojo/public/mojom/base; do
            install -d "${D}/usr/include/libchrome/${d}"
            install -m 0644 "${B}"/gen/include/"${d}"/*.h "${D}/usr/include/libchrome/${d}"
        done
    fi
}

python do_install:append() {
    bb.build.exec_func('other_install', d)
}
FILES:${PN}-dev += "/usr/src/libmojo"
SYSROOT_DIRS += "/usr/src/libmojo"
