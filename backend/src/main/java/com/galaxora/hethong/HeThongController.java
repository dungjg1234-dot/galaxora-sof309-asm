package com.galaxora.hethong;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint kiểm tra backend đang chạy. Không kiểm tra kết nối database.
 */
@RestController
@RequestMapping("/api/he-thong")
public class HeThongController {

    @GetMapping("/suc-khoe")
    public Map<String, String> kiemTraSucKhoe() {
        return Map.of("trangThai", "HOAT_DONG");
    }
}
