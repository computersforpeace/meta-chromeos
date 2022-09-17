do_install:append() {
    # chaps looks for <nss/pkcs11.h>, not <nss3/...>
    ln -sf nss3 ${D}${includedir}/nss
}
