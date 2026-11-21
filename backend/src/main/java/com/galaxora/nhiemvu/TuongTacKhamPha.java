package com.galaxora.nhiemvu;

import static com.galaxora.banhang.LoiBanHang.xungDot;

import java.io.Serializable;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import jakarta.servlet.http.HttpSession;

final class TuongTacKhamPha implements Serializable {
    private static final String KHOA_PHIEN = TuongTacKhamPha.class.getName();
    private static final int DOC_BANG_MILI_GIAY = 900;
    private static final SecureRandom NGAU_NHIEN = new SecureRandom();
    private static final List<CauHoi> CAU_HOI = List.of(
        new CauHoi("Hệ Mặt Trời có bao nhiêu hành tinh, không tính hành tinh lùn?", 8),
        new CauHoi("Trái Đất là hành tinh thứ mấy tính từ Mặt Trời?", 3),
        new CauHoi("Sao Hỏa là hành tinh thứ mấy tính từ Mặt Trời?", 4),
        new CauHoi("Sao Hải Vương là hành tinh thứ mấy tính từ Mặt Trời?", 8),
        new CauHoi("Mặt Trời tự quay khoảng bao nhiêu ngày tại xích đạo?", 25)
    );

    record LanMoBang(UUID ve, int choMiliGiay) {}
    record TinHieu(UUID ve, String cauHoi, int soLanConLai) {}
    private record VeDocBang(UUID ma, Instant duocGhiNhanTu, Instant hetHan) implements Serializable {}
    private record CauHoi(String noiDung, int dapAn) {}
    private record VeTinHieu(UUID ma, int chiSoCauHoi, int soLanConLai, Instant hetHan) implements Serializable {}

    private final long nguoiDungId;
    private final Map<String, VeDocBang> veDocBang = new HashMap<>();
    private VeTinHieu veTinHieu;

    private TuongTacKhamPha(long nguoiDungId) {
        this.nguoiDungId = nguoiDungId;
    }

    static TuongTacKhamPha lay(HttpSession phien, long nguoiDungId) {
        synchronized (phien) {
            if (phien.getAttribute(KHOA_PHIEN) instanceof TuongTacKhamPha cu && cu.nguoiDungId == nguoiDungId) {
                return cu;
            }
            var moi = new TuongTacKhamPha(nguoiDungId);
            phien.setAttribute(KHOA_PHIEN, moi);
            return moi;
        }
    }

    synchronized LanMoBang moBang(String ma, Instant luc) {
        var cu = veDocBang.get(ma);
        if (cu != null && luc.plusMillis(DOC_BANG_MILI_GIAY).isBefore(cu.hetHan())) {
            return new LanMoBang(cu.ma(), DOC_BANG_MILI_GIAY);
        }
        var ve = new VeDocBang(UUID.randomUUID(), luc.plusMillis(DOC_BANG_MILI_GIAY), luc.plusSeconds(120));
        veDocBang.put(ma, ve);
        return new LanMoBang(ve.ma(), DOC_BANG_MILI_GIAY);
    }

    synchronized void xacNhanDocBang(String ma, UUID maVe, Instant luc) {
        var ve = veDocBang.get(ma);
        if (ve == null || !ve.ma().equals(maVe) || !luc.isBefore(ve.hetHan())) {
            throw xungDot("Lượt khám phá đã hết hiệu lực. Hãy mở lại bảng thông tin.");
        }
        if (luc.isBefore(ve.duocGhiNhanTu())) {
            throw xungDot("Hãy giữ bảng thông tin mở thêm một chút.");
        }
        veDocBang.remove(ma);
    }

    synchronized TinHieu moTinHieu(Instant luc) {
        if (veTinHieu == null || !luc.isBefore(veTinHieu.hetHan())) {
            veTinHieu = new VeTinHieu(UUID.randomUUID(), NGAU_NHIEN.nextInt(CAU_HOI.size()), 3, luc.plusSeconds(300));
        }
        if (veTinHieu.soLanConLai() == 0) throw xungDot("Đã hết lượt trả lời. Hãy thử lại sau khi tín hiệu hết hạn.");
        return new TinHieu(veTinHieu.ma(), CAU_HOI.get(veTinHieu.chiSoCauHoi()).noiDung(), veTinHieu.soLanConLai());
    }

    synchronized void xacNhanTinHieu(UUID maVe, int traLoi, Instant luc) {
        var ve = veTinHieu;
        if (ve == null || !ve.ma().equals(maVe) || !luc.isBefore(ve.hetHan())) {
            throw xungDot("Tín hiệu đã hết hiệu lực. Hãy mở lại để nhận câu hỏi.");
        }
        if (ve.soLanConLai() == 0) throw xungDot("Đã hết lượt trả lời. Hãy thử lại sau khi tín hiệu hết hạn.");
        if (CAU_HOI.get(ve.chiSoCauHoi()).dapAn() != traLoi) {
            int soLan = ve.soLanConLai() - 1;
            veTinHieu = new VeTinHieu(ve.ma(), ve.chiSoCauHoi(), soLan, ve.hetHan());
            throw xungDot("Câu trả lời chưa đúng. Số lượt còn lại: " + soLan + ".");
        }
        veTinHieu = null;
    }
}
