package com.galaxora.banhang;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice(basePackages = {"com.galaxora.cuahang", "com.galaxora.kho", "com.galaxora.donhang",
    "com.galaxora.nhiemvu", "com.galaxora.quantri"})
class PhanHoiBanHang {
    @ExceptionHandler(ResponseStatusException.class)
    ProblemDetail loiNghiepVu(ResponseStatusException loi) {
        return loi.getBody();
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ProblemDetail trungHoacSaiRangBuoc() {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, "Dữ liệu trùng hoặc vi phạm ràng buộc.");
    }
}
