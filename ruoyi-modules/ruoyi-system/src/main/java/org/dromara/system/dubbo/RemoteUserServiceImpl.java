package org.dromara.system.dubbo;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.dromara.common.core.enums.UserStatus;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.exception.user.UserException;
import org.dromara.common.core.utils.DateUtils;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.mybatis.helper.DataPermissionHelper;
import org.dromara.common.tenant.helper.TenantHelper;
import org.dromara.system.api.RemoteUserService;
import org.dromara.system.api.domain.bo.RemoteUserBo;
import org.dromara.system.api.model.LoginUser;
import org.dromara.system.api.model.RoleDTO;
import org.dromara.system.api.model.XcxLoginUser;
import org.dromara.system.domain.SysUser;
import org.dromara.system.domain.bo.SysUserBo;
import org.dromara.system.domain.vo.SysDeptVo;
import org.dromara.system.domain.vo.SysRoleVo;
import org.dromara.system.domain.vo.SysUserVo;
import org.dromara.system.mapper.SysDeptMapper;
import org.dromara.system.mapper.SysUserMapper;
import org.dromara.system.service.ISysConfigService;
import org.dromara.system.service.ISysPermissionService;
import org.dromara.system.service.ISysRoleService;
import org.dromara.system.service.ISysUserService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户服务
 *
 * @author shuai.zhou
 */
@RequiredArgsConstructor
@Service
@DubboService
public class RemoteUserServiceImpl implements RemoteUserService {

    private final ISysUserService userService;
    private final ISysPermissionService permissionService;
    private final ISysConfigService configService;
    private final ISysRoleService roleService;
    private final SysDeptMapper deptMapper;
    private final SysUserMapper userMapper;

    @Override
    public LoginUser getUserInfo(String username, String tenantId) throws UserException {
        return TenantHelper.dynamic(tenantId, () -> {
            SysUserVo sysUser = userMapper.selectVoOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUserName, username));
            if (ObjectUtil.isNull(sysUser)) {
                throw new UserException("user.not.exists", username);
            }
            if (UserStatus.DISABLE.getCode().equals(sysUser.getStatus())) {
                throw new UserException("user.blocked", username);
            }
            // 框架登录不限制从什么表查询 只要最终构建出 LoginUser 即可
            // 此处可根据登录用户的数据不同 自行创建 loginUser 属性不够用继承扩展就行了
            return buildLoginUser(sysUser);
        });
    }

    @Override
    public LoginUser getUserInfo(Long userId, String tenantId) throws UserException {
        return TenantHelper.dynamic(tenantId, () -> {
            SysUserVo sysUser = userMapper.selectVoById(userId);
            if (ObjectUtil.isNull(sysUser)) {
                throw new UserException("user.not.exists", "");
            }
            if (UserStatus.DISABLE.getCode().equals(sysUser.getStatus())) {
                throw new UserException("user.blocked", sysUser.getUserName());
            }
            // 框架登录不限制从什么表查询 只要最终构建出 LoginUser 即可
            // 此处可根据登录用户的数据不同 自行创建 loginUser 属性不够用继承扩展就行了
            return buildLoginUser(sysUser);
        });
    }

    @Override
    public LoginUser getUserInfoByPhonenumber(String phonenumber, String tenantId) throws UserException {
        return TenantHelper.dynamic(tenantId, () -> {
            SysUserVo sysUser = userMapper.selectVoOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getPhonenumber, phonenumber));
            if (ObjectUtil.isNull(sysUser)) {
                throw new UserException("user.not.exists", phonenumber);
            }
            if (UserStatus.DISABLE.getCode().equals(sysUser.getStatus())) {
                throw new UserException("user.blocked", phonenumber);
            }
            // 框架登录不限制从什么表查询 只要最终构建出 LoginUser 即可
            // 此处可根据登录用户的数据不同 自行创建 loginUser 属性不够用继承扩展就行了
            return buildLoginUser(sysUser);
        });
    }

    @Override
    public LoginUser getUserInfoByEmail(String email, String tenantId) throws UserException {
        return TenantHelper.dynamic(tenantId, () -> {
            SysUserVo user = userMapper.selectVoOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getEmail, email));
            if (ObjectUtil.isNull(user)) {
                throw new UserException("user.not.exists", email);
            }
            if (UserStatus.DISABLE.getCode().equals(user.getStatus())) {
                throw new UserException("user.blocked", email);
            }
            // 框架登录不限制从什么表查询 只要最终构建出 LoginUser 即可
            // 此处可根据登录用户的数据不同 自行创建 loginUser 属性不够用继承扩展就行了
            return buildLoginUser(user);
        });
    }

    @Override
    public XcxLoginUser getUserInfoByOpenid(String openid) throws UserException {
        // todo 自行实现 userService.selectUserByOpenid(openid);
        SysUser sysUser = new SysUser();
        if (ObjectUtil.isNull(sysUser)) {
            // todo 用户不存在 业务逻辑自行实现
        }
        if (UserStatus.DISABLE.getCode().equals(sysUser.getStatus())) {
            // todo 用户已被停用 业务逻辑自行实现
        }
        // 框架登录不限制从什么表查询 只要最终构建出 LoginUser 即可
        // 此处可根据登录用户的数据不同 自行创建 loginUser 属性不够用继承扩展就行了
        XcxLoginUser loginUser = new XcxLoginUser();
        loginUser.setUserId(sysUser.getUserId());
        loginUser.setUsername(sysUser.getUserName());
        loginUser.setNickname(sysUser.getNickName());
        loginUser.setUserType(sysUser.getUserType());
        loginUser.setOpenid(openid);
        return loginUser;
    }

    @Override
    public Boolean registerUserInfo(RemoteUserBo remoteUserBo) throws UserException, ServiceException {
        SysUserBo sysUserBo = MapstructUtils.convert(remoteUserBo, SysUserBo.class);
        String username = sysUserBo.getUserName();
        boolean exist = TenantHelper.dynamic(remoteUserBo.getTenantId(), () -> {
            if (!("true".equals(configService.selectConfigByKey("sys.account.registerUser")))) {
                throw new ServiceException("当前系统没有开启注册功能");
            }
            return userMapper.exists(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUserName, sysUserBo.getUserName()));
        });
        if (exist) {
            throw new UserException("user.register.save.error", username);
        }
        return userService.registerUser(sysUserBo, remoteUserBo.getTenantId());
    }

    @Override
    public String selectUserNameById(Long userId) {
        return userService.selectUserNameById(userId);
    }

    @Override
    public String selectNicknameById(Long userId) {
        return userService.selectNicknameById(userId);
    }

    /**
     * 构建登录用户
     */
    private LoginUser buildLoginUser(SysUserVo userVo) {
        LoginUser loginUser = new LoginUser();
        loginUser.setTenantId(userVo.getTenantId());
        loginUser.setUserId(userVo.getUserId());
        loginUser.setDeptId(userVo.getDeptId());
        loginUser.setUsername(userVo.getUserName());
        loginUser.setNickname(userVo.getNickName());
        loginUser.setPassword(userVo.getPassword());
        loginUser.setUserType(userVo.getUserType());
        loginUser.setMenuPermission(permissionService.getMenuPermission(userVo.getUserId()));
        loginUser.setRolePermission(permissionService.getRolePermission(userVo.getUserId()));
        SysDeptVo dept = deptMapper.selectVoById(userVo.getDeptId());
        loginUser.setDeptName(ObjectUtil.isNull(dept) ? "" : dept.getDeptName());
        List<SysRoleVo> roles = DataPermissionHelper.ignore(() -> {
            return roleService.selectRolesByUserId(userVo.getUserId());
        });
        loginUser.setRoles(BeanUtil.copyToList(roles, RoleDTO.class));
        return loginUser;
    }

    /**
     * 更新用户信息
     *
     * @param userId 用户ID
     * @param ip     IP地址
     */
    @Override
    public void recordLoginInfo(Long userId, String ip) {
        SysUser sysUser = new SysUser();
        sysUser.setUserId(userId);
        sysUser.setLoginIp(ip);
        sysUser.setLoginDate(DateUtils.getNowDate());
        sysUser.setUpdateBy(userId);
        DataPermissionHelper.ignore(() -> userMapper.updateById(sysUser));
    }

}
