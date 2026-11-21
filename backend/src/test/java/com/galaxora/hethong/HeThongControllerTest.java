package com.galaxora.hethong;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.galaxora.cauhinh.CauHinhBaoMat;
import com.galaxora.taikhoan.DichVuTaiKhoan;

@WebMvcTest(HeThongController.class)
@Import(CauHinhBaoMat.class)
class HeThongControllerTest {

    @MockitoBean
    private DichVuTaiKhoan taiKhoan;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void sucKhoeCongKhaiKhongCanDangNhap() throws Exception {
        mockMvc.perform(get("/api/he-thong/suc-khoe"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.trangThai").value("HOAT_DONG"));
    }

    @Test
    void duongDanKhacYeuCauXacThuc() throws Exception {
        mockMvc.perform(get("/api/khong-ton-tai"))
            .andExpect(status().isUnauthorized());
    }
}
