package com.sky.mapper;

import com.sky.entity.Employee;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);

    /**
     * 插入员工数据
     * @param employee
     */
    @Insert("INSERT INTO `employee` (name, username, password, phone, sex, id_number, " +
            "create_time, update_time, create_user, update_user)" +
            "VALUES (#{name}, #{username}, #{password}, #{phone}, #{sex}, #{idNumber}, " +
            "#{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    void insert(Employee employee);

    /**
     * 根据姓名模糊查询员工
     * @param name
     * @return
     */
    List<Employee> selectByLikeName(String name);

    /**
     * 更新员工数据
     * @param employee
     */
    void update(Employee employee);

}
