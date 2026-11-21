package com.galaxora.nhiemvu;

record CapDoKhamPha(long cap, long mocCapHienTai, long mocCapTiepTheo) {
    static CapDoKhamPha tuTongXp(long tongXp) {
        long cap = 1;
        while (tongXp >= nguong(cap + 1)) cap++;
        return new CapDoKhamPha(cap, nguong(cap), nguong(cap + 1));
    }

    private static long nguong(long cap) {
        return 25 * (cap - 1) * (cap + 2);
    }
}
