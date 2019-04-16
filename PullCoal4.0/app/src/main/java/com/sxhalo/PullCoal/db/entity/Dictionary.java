package com.sxhalo.PullCoal.db.entity;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Mapping;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;
import com.litesuits.orm.db.enums.Relation;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 字典数据
 * Created by amoldZhang on 2017/8/3.
 */
@Table("tb_dictionary_type")
public class Dictionary implements Serializable{

    @PrimaryKey(AssignType.BY_MYSELF)
    @Column(value = "dictionary_data_type")
    public String dataType;
    @Column(value = "dictionary_title")
    public String title;
    @Mapping(Relation.OneToMany)
    public ArrayList<FilterEntity> list;
    public boolean isNameIsChecked() {
        return nameIsChecked;
    }

    public void setNameIsChecked(boolean nameIsChecked) {
        this.nameIsChecked = nameIsChecked;
    }

    private boolean nameIsChecked = true;//默认true 则全部显示  false 则只显示三条

//    @Table("school")
//    public class School{
//        @Mapping(Relation.OneToMany)
//        public ArrayList<Classes> classesList; //一个学校有多个教室
//    }
//
//    @Table("class")public class Classes  {
//        @Mapping(Relation.OneToOne)
//        public Teacher teacher; //一个教室有一个老师，假设
//    }
//
//    @Table("teacher")public class Teacher {
//        @Mapping(Relation.ManyToMany)
//        @MapCollection(ConcurrentLinkedQueue.class)
//        private Queue<Student> studentLinkedQueue; //一个老师多个学生，一个学生多个老师，多对多关系
//    }
//
//    @Table("student")public class Student  {
//        @Mapping(Relation.ManyToMany)
//        private Teacher[] teachersArray;//一个老师多个学生，一个学生多个老师，多对多关系
//    }





}
