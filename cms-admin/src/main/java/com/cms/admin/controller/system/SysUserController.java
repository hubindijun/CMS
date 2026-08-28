package com.cms.admin.controller.system;
import org.springframework.security.access.prepost.PreAuthorize;
import com.cms.admin.dto.system.UserAddDTO;
import com.cms.admin.dto.system.UserQueryDTO;
import com.cms.admin.dto.system.UserUpdateDTO;
import com.cms.admin.service.SysUserService;
import com.cms.admin.vo.system.UserVO;
import com.cms.common.base.PageResult;
import com.cms.common.base.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 系统用户管理接口
 * <p>
 * 提供用户的分页查询、新增、修改、删除、状态切换与重置密码等功能。
 * </p>
 */
@RestController
@RequestMapping("/api/system/user")
@RequiredArgsConstructor
@PreAuthorize("hasRole('admin')")
public class SysUserController {

    private final SysUserService userService;

    /**
     * 分页查询用户列表
     *
     * @param dto 查询条件
     * @return 分页结果
     */
    @GetMapping("/page")
    public Result<PageResult<UserVO>> page(UserQueryDTO dto) {
        return Result.ok(userService.page(dto));
    }

    /**
     * 根据 ID 获取用户详情
     *
     * @param id 用户 ID
     * @return 用户信息
     */
    @GetMapping("/{id}")
    public Result<UserVO> getById(@PathVariable Long id) {
        return Result.ok(userService.getById(id));
    }

    /**
     * 新增用户
     *
     * @param dto 用户信息
     * @return 无
     */
    @PostMapping
    public Result<Void> add(@RequestBody @Valid UserAddDTO dto) {
        userService.add(dto);
        return Result.ok();
    }

    /**
     * 修改用户
     *
     * @param dto 用户信息
     * @return 无
     */
    @PutMapping
    public Result<Void> update(@RequestBody @Valid UserUpdateDTO dto) {
        userService.update(dto);
        return Result.ok();
    }

    /**
     * 删除用户
     *
     * @param id 用户 ID
     * @return 无
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return Result.ok();
    }

    /**
     * 切换用户状态（启用/禁用）
     *
     * @param id     用户 ID
     * @param status 状态值（0 禁用 / 1 启用）
     * @return 无
     */
    @PutMapping("/{id}/status")
    public Result<Void> toggleStatus(@PathVariable Long id, @RequestParam Integer status) {
        userService.toggleStatus(id, status);
        return Result.ok();
    }

    /**
     * 重置用户密码
     *
     * @param id       用户 ID
     * @param password 新密码
     * @return 无
     */
    @PutMapping("/{id}/reset-password")
    public Result<Void> resetPassword(@PathVariable Long id, @RequestParam String password) {
        userService.resetPassword(id, password);
        return Result.ok();
    }
}
