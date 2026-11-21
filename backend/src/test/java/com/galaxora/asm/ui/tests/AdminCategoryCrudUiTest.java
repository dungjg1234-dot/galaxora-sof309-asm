package com.galaxora.asm.ui.tests;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.galaxora.asm.ui.pages.AdminCategoryPage;
import com.galaxora.asm.ui.pages.LoginPage;
import com.galaxora.asm.ui.support.BaseUiTest;

/**
 * Y4 - Selenium + TestNG: Luồng CRUD đầy đủ trên giao diện Quản trị Danh mục (/quan-tri/cua-hang).
 * Dùng dependsOnMethods để bảo đảm thứ tự Create -> Read -> Update -> Delete.
 */
public class AdminCategoryCrudUiTest extends BaseUiTest {

    private static final String SUFFIX = String.valueOf(System.currentTimeMillis() % 100_000L);
    private static final String CREATED_NAME = "Danh mục ASM " + SUFFIX;
    private static final String UPDATED_NAME = "Danh mục ASM Sửa " + SUFFIX;

    private AdminCategoryPage openAsAdmin() {
        new LoginPage(driver, wait).loginAsAdminAndWait();
        return new AdminCategoryPage(driver, wait).open();
    }

    @Test(groups = {"smoke", "regression", "crud"},
          description = "TC-UI-CRUD-01 Create: Admin tạo danh mục mới và thấy ngay trong danh sách")
    public void createCategory() {
        AdminCategoryPage page = openAsAdmin();
        page.createCategory(CREATED_NAME, true);
        Assert.assertTrue(page.hasCategory(CREATED_NAME), "Danh mục vừa tạo phải hiển thị trong danh sách");
    }

    @Test(groups = {"regression", "crud"}, dependsOnMethods = "createCategory",
          description = "TC-UI-CRUD-02 Read: Danh mục vừa tạo tồn tại sau tải lại trang và hiển thị ở Cửa hàng")
    public void readCategoryInAdminAndStorefront() {
        AdminCategoryPage page = openAsAdmin();
        Assert.assertTrue(page.hasCategory(CREATED_NAME), "Danh mục phải tồn tại bền vững sau reload");
        Assert.assertTrue(page.isVisibleInStorefront(CREATED_NAME),
            "Danh mục bật hiển thị phải xuất hiện ở trang công khai /cua-hang");
    }

    @Test(groups = {"regression", "crud"},
          description = "TC-UI-CRUD-03 Validation: Để trống tên danh mục bị trình duyệt chặn gửi form")
    public void createEmptyCategoryIsBlocked() {
        AdminCategoryPage page = openAsAdmin();
        Assert.assertFalse(page.tryCreateEmptyAndCheckValidity(),
            "Form tạo danh mục với tên rỗng phải bị chặn bởi constraint validation");
    }

    @Test(groups = {"regression", "crud"}, dependsOnMethods = "readCategoryInAdminAndStorefront",
          description = "TC-UI-CRUD-04 Update: Admin đổi tên danh mục và kiểm chứng lưu bền vững sau reload")
    public void updateCategory() {
        AdminCategoryPage page = openAsAdmin();
        page.updateCategory(CREATED_NAME, UPDATED_NAME);
        page.open();
        Assert.assertTrue(page.hasCategory(UPDATED_NAME), "Tên danh mục mới phải còn sau reload");
        Assert.assertFalse(page.hasCategory(CREATED_NAME), "Tên danh mục cũ không được còn sau khi đổi tên");
    }

    @Test(groups = {"smoke", "regression", "crud"}, dependsOnMethods = "updateCategory",
          description = "TC-UI-CRUD-05 Delete: Admin xóa danh mục qua hộp thoại xác nhận và xác nhận biến mất sau reload")
    public void deleteCategory() {
        AdminCategoryPage page = openAsAdmin();
        page.deleteCategory(UPDATED_NAME);
        page.open();
        Assert.assertFalse(page.hasCategory(UPDATED_NAME), "Danh mục sau khi xóa không được xuất hiện lại");
    }
}
