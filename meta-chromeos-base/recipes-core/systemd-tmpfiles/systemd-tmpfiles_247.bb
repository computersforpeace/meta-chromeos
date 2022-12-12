DESCRIPTION="Creates, deletes and cleans up volatile and temporary files and directories"
HOMEPAGE="https://www.freedesktop.org/wiki/Software/systemd"
LICENSE = "GPL-2.0-only & LGPL-2.1-only"
LIC_FILES_CHKSUM = " \
    file://LICENSE.GPL2;md5=751419260aa954499f7abaabaa882bbe \
    file://LICENSE.LGPL2.1;md5=4fbd65380cdd255951079008b364516c \
"

SRC_URI = "git://chromium.googlesource.com/chromiumos/third_party/systemd;protocol=https;branch=chromeos-v247"
SRCREV = "58d3fbc2d946d741887b2300d4c70dbe9cddcc7c"

inherit meson pkgconfig

S = "${WORKDIR}/git"

#inherit meson python-any-r1 cros-workon

COMMON_DEPENDS = "\
	acl \
	util-linux \
        libcap \
        libselinux \
"
#selinux? ( sys-libs/libselinux:0= )

DEPENDS += "${COMMON_DEPENDS}"
DEPENDS += "\
    gperf-native \
    rsync-native \
"

RDEPENDS:${PN} += "${COMMON_DEPENDS}"

# For some reason, the systemd-provided headers don't have this, and those
# override the main linux-headers.
CFLAGS += "-DARPHRD_MCTP=290"

# disable everything until configure says "enabled features: ACL, tmpfiles"
EXTRA_OEMESON += "\
    -Dacl=true \
    -Dtmpfiles=true \
    -Dstandalone-binaries=true \
    -Dstatic-libsystemd=true \
    -Dsysvinit-path='' \
    -Dadm-group=false \
    -Danalyze=false \
    -Dapparmor=false \
    -Daudit=false \
    -Dbacklight=false \
    -Dbinfmt=false \
    -Dblkid=false \
    -Dbzip2=false \
    -Dcoredump=false \
    -Ddbus=false \
    -Defi=false \
    -Delfutils=false \
    -Denvironment-d=false \
    -Dfdisk=false \
    -Dgcrypt=false \
    -Dglib=false \
    -Dgshadow=false \
    -Dgnutls=false \
    -Dhibernate=false \
    -Dhostnamed=false \
    -Dhwdb=false \
    -Didn=false \
    -Dima=false \
    -Dinitrd=false \
    -Dfirstboot=false \
    -Dkernel-install=false \
    -Dkmod=false \
    -Dldconfig=false \
    -Dlibcryptsetup=false \
    -Dlibcurl=false \
    -Dlibfido2=false \
    -Dlibidn=false \
    -Dlibidn2=false \
    -Dlibiptc=false \
    -Dlink-networkd-shared=false \
    -Dlink-systemctl-shared=false \
    -Dlink-timesyncd-shared=false \
    -Dlink-udev-shared=false \
    -Dlocaled=false \
    -Dlogind=false \
    -Dlz4=false \
    -Dmachined=false \
    -Dmicrohttpd=false \
    -Dnetworkd=false \
    -Dnss-myhostname=false \
    -Dnss-resolve=false \
    -Dnss-systemd=false \
    -Dopenssl=false \
    -Dp11kit=false \
    -Dpam=false \
    -Dpcre2=false \
    -Dpolkit=false \
    -Dportabled=false \
    -Dpstore=false \
    -Dpwquality=false \
    -Drandomseed=false \
    -Dresolve=false \
    -Drfkill=false \
    -Dseccomp=false \
    -Dsmack=false \
    -Dsysusers=false \
    -Dtimedated=false \
    -Dtimesyncd=false \
    -Dtpm=false \
    -Dqrencode=false \
    -Dquotacheck=false \
    -Duserdb=false \
    -Dutmp=false \
    -Dvconsole=false \
    -Dwheel-group=false \
    -Dxdg-autostart=false \
    -Dxkbcommon=false \
    -Dxz=false \
    -Dzlib=false \
    -Dzstd=false \
"

do_compile() {
	# tmpfiles and sysusers can be built as standalone, link systemd-shared in statically.
	# https://github.com/systemd/systemd/pull/16061 original implementation
	# we just need to pass -Dstandalone-binaries=true and
	# use <name>.standalone target below.
	# check meson.build for if have_standalone_binaries condition per target.
	mytargets="
		systemd-tmpfiles.standalone
	"
		#man/tmpfiles.d.5
		#man/systemd-tmpfiles.8
	meson compile ${mytargets}
}

do_install() {
    install -d ${D}${base_bindir}
    install -m 0755 systemd-tmpfiles.standalone ${D}${base_bindir}/systemd-tmpfiles
}
