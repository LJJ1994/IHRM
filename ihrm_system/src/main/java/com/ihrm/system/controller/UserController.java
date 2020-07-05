package com.ihrm.system.controller;

import com.ihrm.common.constants.PermissionConstants;
import com.ihrm.common.controller.BaseController;
import com.ihrm.common.entity.PageResult;
import com.ihrm.common.entity.Result;
import com.ihrm.common.entity.ResultCode;
import com.ihrm.common.poi.ExcelExportUtil;
import com.ihrm.common.poi.ExcelImportUtil;
import com.ihrm.domain.system.Permission;
import com.ihrm.domain.system.Role;
import com.ihrm.domain.system.User;
import com.ihrm.domain.system.response.ProfileResult;
import com.ihrm.domain.system.response.UserResult;
import com.ihrm.system.poi.SheetHandler;
import com.ihrm.system.service.PermissionService;
import com.ihrm.system.service.UserService;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/sys")
public class UserController extends BaseController {
    @Autowired
    private UserService userService;

    @Autowired
    private PermissionService permissionService;

    /**
     * 用户头像上传
     * @param file
     * @param uid
     * @return
     */
    @RequestMapping(value = "/user/upload/{uid}", method = RequestMethod.POST)
    public Result upload(@RequestParam("file") MultipartFile file,
                         @PathVariable("uid") String uid){
        String upload = userService.upload(uid, file);
        return new Result<>(ResultCode.SUCCESS, upload);
    }

    //保存用户
    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public Result add(@RequestBody User user) throws Exception {
        user.setCompanyId(parseCompanyId());
        user.setCompanyName(parseCompanyName());
        userService.save(user);
        return Result.SUCCESS();
    }

    //更新用户
    @RequestMapping(value = "/user/{id}", method = RequestMethod.PUT)
    public Result update(@PathVariable(name = "id") String id, @RequestBody User user)
            throws Exception {
        user.setId(id);
        userService.update(user);
        return Result.SUCCESS();
    }

    //删除用户
    @RequestMapping(value = "/user/{id}", method = RequestMethod.DELETE)
    public Result delete(@PathVariable(name = "id") String id) throws Exception {
        userService.delete(id);
        return Result.SUCCESS();
    }

    /**
     * 根据ID查询用户
     */
    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
    public Result<UserResult> findById(@PathVariable(name = "id") String id) throws Exception {
        User user = userService.findById(id);
        UserResult userResult = new UserResult(user);
        return new Result<>(ResultCode.SUCCESS, userResult);
    }

    /**
     * 分页查询用户
     */
    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public Result findByPage(Integer page,
                             Integer size,
                             @RequestParam Map<String, Object> map) throws Exception {
        map.put("companyId", parseCompanyId());
        Page<User> searchPage = userService.findSearch(map, page, size);
        PageResult<User> pr = new
                PageResult<>(searchPage.getTotalElements(),searchPage.getContent());
        return new Result<>(ResultCode.SUCCESS, pr);
    }

    /**
     * 查询所有用户，供salarys模块调用
     * @return
     */
    @RequestMapping(value = "/user/list", method = RequestMethod.GET)
    public Result findAll(){
        List<User> all = userService.findAll();
        return new Result<>(ResultCode.SUCCESS, all);
    }

    @RequestMapping(value = "/user/assignRoles", method = RequestMethod.PUT)
    public Result assignRole(@RequestBody Map<String, Object> map){
        String uid = (String) map.get("id");
        List<String> roleIds = (List<String>) map.get("roleIds");
        userService.assignRoles(uid, roleIds);
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 用户登录
     *  1.通过service根据mobile查询用户
     *  2.比较password
     *  3.生成jwt信息
     *
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Result<String> login(@RequestBody Map<String, Object> map){
//        String mobile = (String) map.get("mobile");
//        String password = (String) map.get("password");
//        User user = userService.findByMobilAndPassword(mobile, password);
//        if (user == null){
//            return new Result<>(ResultCode.LOGIN_ERROR);
//        }
//        //构造权限api字符串，放到token中
//        StringBuilder sb = new StringBuilder();
//        for (Role role : user.getRoles()) {
//            for (Permission permission : role.getPermissions()) {
//                if (permission.getType() == PermissionConstants.PERMISSION_API){
//                    sb.append(permission.getCode()).append(",");
//                }
//            }
//        }
//        Map<String, Object> jwtMap = new HashMap<>();
//        jwtMap.put("apis", sb.toString());
//        jwtMap.put("companyId", user.getCompanyId());
//        jwtMap.put("companyName", user.getCompanyName());
//        String token = jwtUtils.createJwt(user.getId(), user.getUsername(), jwtMap);
//        return new Result<>(ResultCode.LOGIN_SUCCESS, token);

        String mobile = (String) map.get("mobile");
        String password = (String) map.get("password");

        password = new Md5Hash(password, mobile, 3).toString();
        UsernamePasswordToken token = new UsernamePasswordToken(mobile, password);
        Subject subject = SecurityUtils.getSubject();
        subject.login(token);
        // 获取先前放置到shiro中的sessionid
        String sessionId = (String) subject.getSession().getId();
        return new Result<>(ResultCode.SUCCESS, sessionId);
    }

    /**
     * 登录成功，通过token获取个人信息
     * @param request
     * @return
     */
    @RequestMapping(value = "/profile", method = RequestMethod.POST)
    public Result profile(HttpServletRequest request) {
        // 1. JWT版本
//        String authorization = request.getHeader("Authorization");
//        if (StringUtils.isEmpty(authorization)) {
//            return new Result(ResultCode.UNAUTHENCATED); // 未认证
//        }
//        String token = authorization.replace("Bearer ", "");
//        Claims claims = jwtUtils.parseJwt(token);
//        String userId = claims.getId();
//        User user = userService.findById(userId);
//        ProfileResult userResult = null;
//        // 如果是普通用户
//        if ("user".equals(user.getLevel())){
//            userResult = new ProfileResult(user);
//        }else{
//            Map<String, Object> map = new HashMap<>();
//            if ("coAdmin".equals(user.getLevel())){
//                map.put("enVisible", "1");
//            }
//            List<Permission> permissionList = permissionService.findAll(map);
//            userResult = new ProfileResult(user, permissionList);
//        }

        // 2. Shiro版本
        Subject subject = SecurityUtils.getSubject();
        PrincipalCollection principals = subject.getPrincipals();
        if (principals == null) {
            return new Result(ResultCode.UNAUTHENCATED);
        }
        ProfileResult profileResult = (ProfileResult) principals.getPrimaryPrincipal();
        return new Result<>(ResultCode.SUCCESS, profileResult);
    }

    /**
     * 工具类excel导入，批量保存用户
     * @param file
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/user/import", method = RequestMethod.POST)
    public Result importExcel(@RequestParam("file") MultipartFile file) throws IOException {
        List<User> list = new ExcelImportUtil<User>(User.class).readExcel(file.getInputStream(), 1, 1);
        userService.saveAll(list, companyId, companyName);
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 代码编写导入Excel，批量保存用户
     * @param file
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/user/import01", method = RequestMethod.POST)
    public Result importExcel01(@RequestParam("file") MultipartFile file) throws IOException {
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        List<User> list = new ArrayList<>();
        for (int rowNum = 1; rowNum < sheet.getLastRowNum(); rowNum++) {
            Row row = sheet.getRow(rowNum);
            Object[] objs = new Object[row.getLastCellNum()];
            for (int cellNum = 1; cellNum < row.getLastCellNum(); cellNum++) {
                Cell cell = row.getCell(cellNum);
                objs[cellNum] = ExcelImportUtil.getValue(cell);
            }
            User user = new User(objs);
            list.add(user);
        }
        userService.saveAll(list, companyId, companyName);
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 基于事件驱动，导入百万数据报表
     * @param file
     * @return
     */
    @RequestMapping(value = "/user/import02", method = RequestMethod.POST)
    public Result importExcel02(@RequestParam("file") MultipartFile file) throws IOException, OpenXML4JException, SAXException {
        //1.根据excel报表获取OPCPackage
        OPCPackage opcPackage = OPCPackage.open((File) file, PackageAccess.READ);
        //2.创建XSSFReader
        XSSFReader reader = new XSSFReader(opcPackage);
        //3.获取SharedStringTable对象
        SharedStringsTable table = reader.getSharedStringsTable();
        //4.获取styleTable对象
        StylesTable stylesTable = reader.getStylesTable();
        //5.创建Sax的xmlReader对象
        XMLReader xmlReader = XMLReaderFactory.createXMLReader();
        //6.注册事件处理器
        XSSFSheetXMLHandler xmlHandler = new XSSFSheetXMLHandler(stylesTable,table, new SheetHandler(),false);
        xmlReader.setContentHandler(xmlHandler);
        //7.逐行读取
        XSSFReader.SheetIterator sheetIterator = (XSSFReader.SheetIterator) reader.getSheetsData();
        while (sheetIterator.hasNext()) {
            InputStream stream = sheetIterator.next(); //每一个sheet的流数据
            InputSource is = new InputSource(stream);
            xmlReader.parse(is);
        }
        return new Result(ResultCode.SUCCESS);
    }
}
