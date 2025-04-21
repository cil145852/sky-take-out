package com.sky.mapper;

import com.sky.entity.Employee;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EmployeeMapper {

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

    /**
     * 根据员工数据查询员工
     * @param employee 查询条件
     * @return
     */
    List<Employee> selectList(Employee employee);

}
