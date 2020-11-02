-- add by wenxq 健康处方保存
alter table PUB_PELPLEHEALTHTEACH_MB add diagnoseid VARCHAR2(18);
-- Add comments to the columns 
comment on column PUB_PELPLEHEALTHTEACH_MB.diagnoseid
  is '诊断ID';
--add by caijy at 2015-11-27 for 药房药品养护
-- add by gaof 药房药品养护
create table YF_YH01
(
  JGID VARCHAR2(20) default 1 not null,
  XTSB NUMBER(18) not null,
  YHDH VARCHAR2(12) not null,
  YPLB VARCHAR2(16),
  KWLB VARCHAR2(16),
  BZXX VARCHAR2(100),
  YHRQ DATE,
  CZGH VARCHAR2(10),
  YSGH VARCHAR2(10),
  ZXRQ DATE
);
-- Add comments to the table 
comment on table YF_YH01
  is '药房药品养护表';
-- Add comments to the columns 
comment on column YF_YH01.JGID
  is '机构ID';
comment on column YF_YH01.XTSB
  is '药库识别';
comment on column YF_YH01.YHDH
  is '养护单号';
comment on column YF_YH01.YPLB
  is '药品类别';
comment on column YF_YH01.KWLB
  is '库位类别';
comment on column YF_YH01.BZXX
  is '备注信息';
comment on column YF_YH01.YHRQ
  is '养护日期';
comment on column YF_YH01.CZGH
  is '操作工号';
comment on column YF_YH01.YSGH
  is '验收工号';
comment on column YF_YH01.ZXRQ
  is '执行日期';
-- Create/Recreate primary, unique and foreign key constraints 
alter table YF_YH01
  add constraint PK_YF_YH01 primary key (XTSB, YHDH)
  using index ;
  
 -- Create table
create table YF_YH02
(
  JGID      VARCHAR2(20) default 1 not null,
  SBXH      NUMBER(18) not null,
  XTSB      NUMBER(18) not null,
  YHDH      VARCHAR2(12) not null,
  YPXH      NUMBER(18) not null,
  YPCD      NUMBER(18) not null,
  YPPH      VARCHAR2(20),
  YPXQ      DATE,
  KCSL      NUMBER(10,4) default 0,
  TYPE      NUMBER(6) default 0,
  JHRQ      DATE,
  PFJG      NUMBER(12,6) default 0,
  KCSB      NUMBER(18) default 0,
  FXWTYPJCL VARCHAR2(200)
);
-- Add comments to the table 
comment on table YF_YH02
  is '药房药品养护明细表';
-- Add comments to the columns 
comment on column YF_YH02.JGID
  is '机构ID';
comment on column YF_YH02.SBXH
  is '识别序号';
comment on column YF_YH02.XTSB
  is '药库识别';
comment on column YF_YH02.YHDH
  is '养护单号';
comment on column YF_YH02.YPXH
  is '药品序号';
comment on column YF_YH02.YPCD
  is '药品产地';
comment on column YF_YH02.YPPH
  is '药品批号';
comment on column YF_YH02.YPXQ
  is '药品效期';
comment on column YF_YH02.KCSL
  is '库存数量';
comment on column YF_YH02.TYPE
  is '库存性质';
comment on column YF_YH02.JHRQ
  is '进货日期';
comment on column YF_YH02.PFJG
  is '批发价格';
comment on column YF_YH02.KCSB
  is '库存识别';
comment on column YF_YH02.FXWTYPJCL
  is '发现问题药品及处理';
-- Create/Recreate primary, unique and foreign key constraints 
alter table YF_YH02
  add constraint PK_YF_YH02 primary key (SBXH)
  using index;
 
--add by caijy at 2015-11-26 for 危机值
-- Create table
create table JG_WJZCYY
(
  JLXH NUMBER(10) not null,
  CYY  VARCHAR2(100),
  SXH  NUMBER(6)
);
-- Add comments to the columns 
comment on column JG_WJZCYY.JLXH
  is '记录序号';
comment on column JG_WJZCYY.CYY
  is '常用语';
comment on column JG_WJZCYY.SXH
  is '顺序号';
-- Create/Recreate primary, unique and foreign key constraints 
alter table JG_WJZCYY
  add constraint PK_JG_WJZCYY primary key (JLXH)
  using index ;

  -- Create table
create table JG_WJZCZ
(
  JLXH      NUMBER(18) not null,
  WJZXH     NUMBER(18),
  CZSJ      DATE,
  CZKS      VARCHAR2(10),
  CZRY      VARCHAR2(10),
  CZRYXM    VARCHAR2(20),
  CZNR      VARCHAR2(250),
  TIMESTAMP DATE
);
-- Add comments to the table 
comment on table JG_WJZCZ
  is '危机值操作记录';
-- Add comments to the columns 
comment on column JG_WJZCZ.JLXH
  is '记录序号';
comment on column JG_WJZCZ.WJZXH
  is '危急值序号';
comment on column JG_WJZCZ.CZSJ
  is '操作时间';
comment on column JG_WJZCZ.CZKS
  is '操作科室';
comment on column JG_WJZCZ.CZRY
  is '操作人员';
comment on column JG_WJZCZ.CZRYXM
  is '操作人员姓名';
comment on column JG_WJZCZ.CZNR
  is '简短描述';
comment on column JG_WJZCZ.TIMESTAMP
  is '时间戳';
-- Create/Recreate primary, unique and foreign key constraints 
alter table JG_WJZCZ
  add constraint PK_JG_WJZCZ primary key (JLXH)
  using index ;

  -- Create table
create table JG_WJZJL
(
  JLXH      NUMBER(18) not null,
  JZLB      NUMBER(2),
  JZXH      VARCHAR2(20),
  CARDID    VARCHAR2(20),
  ZYHM      VARCHAR2(20),
  BRXM      VARCHAR2(20),
  BRXB      CHAR(1),
  CSRQ      DATE,
  BRKS      VARCHAR2(10),
  BRBQ      VARCHAR2(10),
  BRCH      VARCHAR2(10),
  ZRYS      VARCHAR2(10),
  ZRYSXM    VARCHAR2(20),
  JTYS      VARCHAR2(10),
  JTYSXM    VARCHAR2(20),
  XMLB      NUMBER(2),
  XMDM      VARCHAR2(20),
  XMMC      VARCHAR2(100),
  XMJG      VARCHAR2(100),
  XMDW      VARCHAR2(10),
  CKFW      VARCHAR2(50),
  WJZFW     VARCHAR2(50),
  SHKS      VARCHAR2(10),
  SHSJ      DATE,
  SHRY      VARCHAR2(10),
  SHRYXM    VARCHAR2(20),
  FBZT      NUMBER(1),
  FBSJ      DATE,
  FBRY      VARCHAR2(10),
  FBRYXM    VARCHAR2(20),
  CLZT      NUMBER(1),
  CLSJ      DATE,
  CLRY      VARCHAR2(10),
  CLRYXM    VARCHAR2(20),
  CLQK      VARCHAR2(250),
  BGDH      VARCHAR2(20),
  TXNR      VARCHAR2(200),
  ZDID      VARCHAR2(10),
  LCZD      VARCHAR2(80),
  TIMESTAMP DATE
);
-- Add comments to the table 
comment on table JG_WJZJL
  is '危机值记录';
-- Add comments to the columns 
comment on column JG_WJZJL.JLXH
  is '记录序号';
comment on column JG_WJZJL.JZLB
  is '就诊类别';
comment on column JG_WJZJL.JZXH
  is '就诊序号';
comment on column JG_WJZJL.CARDID
  is '病人卡号';
comment on column JG_WJZJL.ZYHM
  is '住院号码';
comment on column JG_WJZJL.BRXM
  is '病人姓名';
comment on column JG_WJZJL.BRXB
  is '病人性别';
comment on column JG_WJZJL.CSRQ
  is '出生日期';
comment on column JG_WJZJL.BRKS
  is '病人科室';
comment on column JG_WJZJL.BRBQ
  is '病人病区';
comment on column JG_WJZJL.BRCH
  is '病人床号';
comment on column JG_WJZJL.ZRYS
  is '责任医生';
comment on column JG_WJZJL.ZRYSXM
  is '责任医生姓名';
comment on column JG_WJZJL.JTYS
  is '家庭医生';
comment on column JG_WJZJL.JTYSXM
  is '家庭医生姓名';
comment on column JG_WJZJL.XMLB
  is '1:检验；2:放射；3:超声；4:心电';
comment on column JG_WJZJL.XMDM
  is '项目代码';
comment on column JG_WJZJL.XMMC
  is '项目名称';
comment on column JG_WJZJL.XMJG
  is '项目结果';
comment on column JG_WJZJL.XMDW
  is '项目单位';
comment on column JG_WJZJL.CKFW
  is '参考范围';
comment on column JG_WJZJL.WJZFW
  is '危急值范围';
comment on column JG_WJZJL.SHKS
  is '审核科室';
comment on column JG_WJZJL.SHSJ
  is '审核时间';
comment on column JG_WJZJL.SHRY
  is '审核人员';
comment on column JG_WJZJL.SHRYXM
  is '审核人员姓名';
comment on column JG_WJZJL.FBZT
  is '1.及时 2.延后 3.未发布';
comment on column JG_WJZJL.FBSJ
  is '发布时间';
comment on column JG_WJZJL.FBRY
  is '发布人员';
comment on column JG_WJZJL.FBRYXM
  is '发布人员姓名';
comment on column JG_WJZJL.CLZT
  is '处理状态';
comment on column JG_WJZJL.CLSJ
  is '处理时间';
comment on column JG_WJZJL.CLRY
  is '处理人员';
comment on column JG_WJZJL.CLRYXM
  is '处理人员姓名';
comment on column JG_WJZJL.CLQK
  is '处理情况';
comment on column JG_WJZJL.BGDH
  is '报告单号';
comment on column JG_WJZJL.TXNR
  is '提醒内容';
comment on column JG_WJZJL.ZDID
  is '诊断ID';
comment on column JG_WJZJL.LCZD
  is '临床诊断';
comment on column JG_WJZJL.TIMESTAMP
  is '时间戳';
-- Create/Recreate primary, unique and foreign key constraints 
alter table JG_WJZJL
  add constraint PK_JG_WJZJL primary key (JLXH)
  using index ;

  -- Create table
create table JG_WJZTXFJ
(
  YWLB   NUMBER(1) not null,
  TXJB   NUMBER(1) not null,
  TXJBMC VARCHAR2(20),
  TXSJ   NUMBER(2)
);
-- Add comments to the table 
comment on table JG_WJZTXFJ
  is '危急值提醒分级';
-- Add comments to the columns 
comment on column JG_WJZTXFJ.YWLB
  is '业务类别';
comment on column JG_WJZTXFJ.TXJB
  is '提醒级别';
comment on column JG_WJZTXFJ.TXJBMC
  is '提醒级别名称';
comment on column JG_WJZTXFJ.TXSJ
  is '提醒时间';
-- Create/Recreate primary, unique and foreign key constraints 
alter table JG_WJZTXFJ
  add constraint PK_JG_WJZTXFJ primary key (YWLB, TXJB)
  using index ;

  -- Create table
create table JG_WJZTXFJ_KSRY
(
  YWLB NUMBER(1) not null,
  TXJB NUMBER(1) not null,
  KSDM NUMBER(4) not null,
  YGDM VARCHAR2(10) not null,
  SJHM VARCHAR2(20)
);
-- Add comments to the table 
comment on table JG_WJZTXFJ_KSRY
  is '危急值提醒分级科室人员信息';
-- Add comments to the columns 
comment on column JG_WJZTXFJ_KSRY.YWLB
  is '业务类别';
comment on column JG_WJZTXFJ_KSRY.TXJB
  is '提醒级别';
comment on column JG_WJZTXFJ_KSRY.KSDM
  is '科室代码';
comment on column JG_WJZTXFJ_KSRY.YGDM
  is '员工代码';
comment on column JG_WJZTXFJ_KSRY.SJHM
  is '手机号码';
-- Create/Recreate primary, unique and foreign key constraints 
alter table JG_WJZTXFJ_KSRY
  add constraint PK_JG_WJZTXFJ_KSRY primary key (YWLB, TXJB, KSDM, YGDM)
  using index ;
--add by gaof at 2015-09-7 
alter table DR_CLINICRECORDLHISTORY modify  ZHUANZHENZYSX varchar2(1000);

--add by yangl at 2015-09-08
alter table ZY_RYZD modify ICD10 VARCHAR2(20);

--add by zhangxw at 2015-09-7 for 检验接口（连接池注册时使用）
-- Create table
create table GY_LJJG
(
  JGID NUMBER(8) not null,
  LJID VARCHAR2(10) not null,
  ZCBZ NUMBER(3) default 0
);
-- Add comments to the table 
comment on table GY_LJJG
  is '连接机构（和CSB版检验系统对接时，连接池注册操作使用）';
-- Add comments to the columns 
comment on column GY_LJJG.JGID
  is '机构id';
comment on column GY_LJJG.LJID
  is '连接id';
comment on column GY_LJJG.ZCBZ
  is '注册标志';
-- Create/Recreate primary, unique and foreign key constraints 
alter table GY_LJJG
  add constraint PK_GY_LJJG primary key (JGID, LJID)
 ;

--add by gaof at 2015-08-28 
alter table DR_CLINICRECORDLHISTORY modify  BINQINGMS varchar2(1000);

--add by yaosq 2015-08-27
-- 门诊首页增加身高体重bmi
alter table MS_BCJL add W INTEGER;
alter table MS_BCJL add H INTEGER;
alter table MS_BCJL add BMI NUMBER(6,2);
alter table MS_BCJL add DPY VARCHAR2(10);
comment on column MS_BCJL.DPY
  is '代配药';


--add by caijy at 2015-08-19 for 医保
drop table YB_YPXX;
create table YB_YPXX
(
  YBYPBM VARCHAR2(20) not null,
  YBYPMC VARCHAR2(50) not null,
  PYDM VARCHAR2(10) not null
);
-- Add comments to the table 
comment on table YB_YPXX
  is '医保药品表';
-- Add comments to the columns 
comment on column YB_YPXX.YBYPBM
  is '医保对应的药品编码';
  comment on column YB_YPXX.YBYPMC
  is '医保对应的药品名称';
  comment on column YB_YPXX.PYDM
  is '拼音代码';
-- Create/Recreate primary, unique and foreign key constraints 
alter table YB_YPXX
  add constraint PK_YB_YPXX primary key (YBYPBM)
  using index ;
  
  drop table YB_FYXX;
create table YB_FYXX
(
  YBFYBM VARCHAR2(20) not null,
  YBFYMC VARCHAR2(50) not null,
  PYDM VARCHAR2(10) not null
);
-- Add comments to the table 
comment on table YB_FYXX
  is '医保费用表';
-- Add comments to the columns 
comment on column YB_FYXX.YBFYBM
  is '医保对应的费用编码';
  comment on column YB_FYXX.YBFYMC
  is '医保对应的费用名称';
  comment on column YB_FYXX.PYDM
  is '拼音代码';
-- Create/Recreate primary, unique and foreign key constraints 
alter table YB_FYXX
  add constraint PK_YB_FYXX primary key (YBFYBM)
  using index ;
drop table YB_YPDZ;
create table YB_YPDZ
(
  YPXH   NUMBER(18) not null,
  YBYPBM VARCHAR2(20) not null,
  YBYPMC VARCHAR2(50) not null
);
-- Add comments to the table 
comment on table YB_YPDZ
  is '医保药品对照表';
-- Add comments to the columns 
comment on column YB_YPDZ.YPXH
  is '药品序号';
comment on column YB_YPDZ.YBYPBM
  is '医保对应的药品编码';
  comment on column YB_YPDZ.YBYPMC
  is '医保对应的药品名称';
-- Create/Recreate primary, unique and foreign key constraints 
alter table YB_YPDZ
  add constraint PK_YB_YPDZ primary key (YPXH)
  using index ;

drop table YB_FYDZ;
create table YB_FYDZ
(
  FYXH   NUMBER(18) not null,
  YBFYBM VARCHAR2(20) not null,
  YBFYMC VARCHAR2(50) not null
);
-- Add comments to the table 
comment on table YB_FYDZ
  is '医保费用对照表';
-- Add comments to the columns 
comment on column YB_FYDZ.FYXH
  is '费用序号';
comment on column YB_FYDZ.YBFYBM
  is '医保对应的费用编码';
  comment on column YB_FYDZ.YBFYMC
  is '医保对应的费用名称';
-- Create/Recreate primary, unique and foreign key constraints 
alter table YB_FYDZ
  add constraint PK_YB_FYDZ primary key (FYXH)
  using index ;
--add by caijy at 2015-07-16 for 病人性质增加性质小类用于判断医保类型
alter table GY_BRXZ add XZXL number(2) default 0;
comment on column GY_BRXZ.XZXL
  is '性质控制小类,暂时定为 1是市医保  其他类别自行添加, 医保判断为XZDL为1 才会生效';

--add by gaof 2015-8-19
alter table DR_CLINICCHECKHISTORY modify  SONGJIANKS varchar(32);
alter table DR_CLINICXXEQUIPMENTHISTORY modify  YUYUESJ varchar(20);

--add by zhangyq 2015-5-12
-- Create table
create table ZY_CKWH
(
  KSDM  NUMBER(18) not null,
  BRXZ  NUMBER(18) not null,
  JGID  VARCHAR2(20) not null,
  CKBL  NUMBER(3,2) default 0,
  CKJE  NUMBER(6,2) default 0,
  DJJE  NUMBER(6,2) default 0,
  ZDXE  NUMBER(6,2) default 0
);
-- Add comments to the table 
comment on table ZY_CKWH
  is '住院_催款维护';
-- Add comments to the columns 
comment on column ZY_CKWH.KSDM
  is '科室(病区)名称';
comment on column ZY_CKWH.BRXZ
  is '病人性质(住院)';
comment on column ZY_CKWH.JGID
  is '机构ID';
comment on column ZY_CKWH.CKBL
  is '催款比例';
comment on column ZY_CKWH.CKJE
  is '催款金额';
comment on column ZY_CKWH.DJJE
  is '冻结金额';
comment on column ZY_CKWH.ZDXE
  is '最低限额';

-- Create/Recreate primary, unique and foreign key constraints 
alter table ZY_CKWH
  add constraint PK_ZY_CKWH primary key (KSDM, BRXZ, JGID);

 --add by gaof 2015-5-6
alter table ZY_RYZD add  ZDMC varchar2(80);
comment on column ZY_RYZD.ZDMC  is '诊断名称';
alter table ZY_RYZD add  ICD10 varchar2(10);
comment on column ZY_RYZD.ICD10  is 'ICD10';
alter table ZY_RYZD add  ZXLB number(6);
comment on column ZY_RYZD.ZXLB  is '中西类别';
alter table ZY_RYZD add  ZDBW number(12);
comment on column ZY_RYZD.ZDBW  is '部位/证侯';
alter table ZY_RYZD add  ZDYS varchar2(8);
comment on column ZY_RYZD.ZDYS  is '诊断医生';
alter table ZY_RYZD add  ZDSJ date;
comment on column ZY_RYZD.ZDSJ  is '诊断时间';
alter table ZY_RYZD add  JBPB varchar2(8);
comment on column ZY_RYZD.JBPB  is '疾病判别';

  --add by shiwy 2015-05-04
alter table DR_CLINICRECORDLHISTORY add ZHUANRUYYDM varchar2(30);
comment on column DR_CLINICRECORDLHISTORY.ZHUANRUYYDM
  is '转入医院代码';
alter table DR_CLINICRECORDLHISTORY add ZHUANRUYYMC varchar2(50);
comment on column DR_CLINICRECORDLHISTORY.ZHUANRUYYMC
  is '转入医院名称';
alter table DR_CLINICRECORDLHISTORY add ZHUANRUKSDM varchar2(30);
comment on column DR_CLINICRECORDLHISTORY.ZHUANRUKSDM
  is '转入科室代码';
alter table DR_CLINICRECORDLHISTORY add ZHUANRUKSMC varchar2(50);
comment on column DR_CLINICRECORDLHISTORY.ZHUANRUKSMC
  is '转入科室名称';
 create table TWR_HZDJ
(
  ID     VARCHAR2(16) not null,
  EMPIID VARCHAR2(32) not null,
  MZHM   VARCHAR2(32),
  ZCJG   VARCHAR2(50),
  JGDH   VARCHAR2(20),
  ZCYS   VARCHAR2(50),
  YSDH   VARCHAR2(20),
  ZDXH   VARCHAR2(18),
  ZLJG   VARCHAR2(1000),
  ZLGC   VARCHAR2(1000),
  XYBZLFAHYJ   VARCHAR2(1000),
  CLQKXX   VARCHAR2(1000)
);
-- Add comments to the table 
comment on table TWR_HZDJ
  is '检查申请记录';
-- Add comments to the columns 
comment on column TWR_HZDJ.ID
  is '记录序号';
comment on column TWR_HZDJ.EMPIID
  is 'EMPIID';
comment on column TWR_HZDJ.MZHM
  is '门诊号码';
comment on column TWR_HZDJ.ZCJG
  is '转出机构';
comment on column TWR_HZDJ.JGDH
  is '机构电话';
comment on column TWR_HZDJ.ZCYS
  is '转出医生';
comment on column TWR_HZDJ.YSDH
  is '就诊卡号';
comment on column TWR_HZDJ.ZDXH
  is '送检医生';
comment on column TWR_HZDJ.ZLJG
  is '治疗结果';
comment on column TWR_HZDJ.ZLGC
  is '治疗经过';
comment on column TWR_HZDJ.XYBZLFAHYJ
is '下一步治疗方案和意见';
comment on column TWR_HZDJ.CLQKXX
is '处理情况信息';
-- Create/Recreate primary, unique and foreign key constraints 
alter table TWR_HZDJ add constraint PK_TWR_HZDJ primary key (ID);
 
 --add by shiwy 2015-04-23
 alter table DR_CLINICRECORDLHISTORY add ZHUANZHENZD varchar2(50);
comment on column DR_CLINICRECORDLHISTORY.ZHUANZHENZD
  is '转诊诊断';
 --add by yangl 2015-04-21
alter table JC_BRRY add CCQK VARCHAR2(255);
-- Add comments to the columns 
comment on column JC_BRRY.CCQK
  is '撤床情况';
 
  --2015.4.21 update by shiwy
  -- Create table
drop table DR_CLINICCHECKHISTORY;
create table DR_CLINICCHECKHISTORY
(
  ID           VARCHAR2(16) not null,
  EMPIID       VARCHAR2(32) not null,
  MZHM         VARCHAR2(32),
  STATUS       VARCHAR2(10),
  SONGJIANRQ   DATE,
  JIUZHENKLX   VARCHAR2(10),
  JIUZHENKH    VARCHAR2(10),
  SONGJIANYS   VARCHAR2(10),
  SONGJIANKS   VARCHAR2(20),
  SONGJIANKSMC VARCHAR2(32),
  SHOUFEISB    VARCHAR2(5),
  BINGQINGMS   VARCHAR2(100),
  ZHENDUAN     VARCHAR2(100),
  BINGRENTZ    VARCHAR2(100),
  QITAJC       VARCHAR2(100),
  BINGRENZS    VARCHAR2(100),
  JIANCHALY    VARCHAR2(10),
  BINGRENXM    VARCHAR2(10),
  BINGRENSFZH  VARCHAR2(18),
  JIESHOUFS    VARCHAR2(10),
  JIANCHASQDH  VARCHAR2(10),
  SHENQINGRQ   VARCHAR2(12)
);
alter table DR_CLINICCHECKHISTORY add constraint PK_DR_CLINICCHECKHISTORY primary key (ID);
-- Add comments to the table 
comment on table DR_CLINICCHECKHISTORY
  is '检查申请记录';
-- Add comments to the columns 
comment on column DR_CLINICCHECKHISTORY.ID
  is '记录序号';
comment on column DR_CLINICCHECKHISTORY.EMPIID
  is 'EMPIID';
comment on column DR_CLINICCHECKHISTORY.MZHM
  is '门诊号码';
comment on column DR_CLINICCHECKHISTORY.STATUS
  is '送检状态';
comment on column DR_CLINICCHECKHISTORY.SONGJIANRQ
  is '送检日期';
comment on column DR_CLINICCHECKHISTORY.JIUZHENKLX
  is '就诊卡类型 2，社保卡，3，就诊卡';
comment on column DR_CLINICCHECKHISTORY.JIUZHENKH
  is '就诊卡号';
comment on column DR_CLINICCHECKHISTORY.SONGJIANYS
  is '送检医生';
comment on column DR_CLINICCHECKHISTORY.SONGJIANKS
  is '送检机构代码';
comment on column DR_CLINICCHECKHISTORY.SONGJIANKSMC
  is '送检机构名称';
comment on column DR_CLINICCHECKHISTORY.SHOUFEISB
  is '收费识别  默认0      0未收费1已收费';
comment on column DR_CLINICCHECKHISTORY.BINGQINGMS
  is '病情描述';
comment on column DR_CLINICCHECKHISTORY.ZHENDUAN
  is '诊断';
comment on column DR_CLINICCHECKHISTORY.BINGRENTZ
  is '病人体征';
comment on column DR_CLINICCHECKHISTORY.QITAJC
  is '其它检查';
comment on column DR_CLINICCHECKHISTORY.BINGRENZS
  is '病人主诉';
comment on column DR_CLINICCHECKHISTORY.JIANCHALY
  is '检查来源  1院内2社区';
comment on column DR_CLINICCHECKHISTORY.BINGRENXM
  is '病人姓名';
comment on column DR_CLINICCHECKHISTORY.BINGRENSFZH
  is '病人身份证号';
comment on column DR_CLINICCHECKHISTORY.JIESHOUFS
  is '接收方式  0或空：本地保存 1：插费用表（社区传0）';
comment on column DR_CLINICCHECKHISTORY.JIANCHASQDH
  is '检查申请单号';
comment on column DR_CLINICCHECKHISTORY.SHENQINGRQ
  is '申请日期';
  --2015.4.20 update by shiwy
  alter table DR_CLINICRECORDLHISTORY modify ZHUANZHENDH varchar2(16);
  comment on column DR_CLINICRECORDLHISTORY.ZHUANZHENDH is '转诊单号';
  alter table DR_CLINICRECORDLHISTORY modify ZHUANZHENRQ varchar2(19);
  comment on column DR_CLINICRECORDLHISTORY.ZHUANZHENRQ is '转诊日期';
  alter table DR_CLINICRECORDLHISTORY modify SHENQINGRQ varchar2(19);
  comment on column DR_CLINICRECORDLHISTORY.SHENQINGRQ is '申请日期';
--2015.4.17 update by shiwy
-- Create table
drop table DR_CLINICRECORDLHISTORY;
create table DR_CLINICRECORDLHISTORY
(
  ID             VARCHAR2(16) not null,
  EMPIID         VARCHAR2(32) not null,
  MZHM           VARCHAR2(32),
  STATUS         VARCHAR2(10),
  JIUZHENKLX     VARCHAR2(10),
  JIUZHENKH      VARCHAR2(16),
  YIBAOKLX       VARCHAR2(10),
  YIBAOKXX       VARCHAR2(32),
  YEWULX         VARCHAR2(10),
  BINGRENXM      VARCHAR2(10),
  BINGRENXB      VARCHAR2(10),
  BINGRENCSRQ    VARCHAR2(16),
  BINGRENNL      VARCHAR2(5),
  BINGRENSFZH    VARCHAR2(18),
  BINGRENLXDH    VARCHAR2(15),
  BINGRENLXDZ    VARCHAR2(50),
  BINGRENFYLB    VARCHAR2(10),
  SHENQINGJGDM   VARCHAR2(20),
  SHENQINGJGMC   VARCHAR2(32),
  SHENQINGJGLXDH VARCHAR2(15),
  SHENQINGYSDH   VARCHAR2(15),
  ZHUANZHENYY    VARCHAR2(100),
  BINQINGMS      VARCHAR2(100),
  ZHUANZHENZYSX  VARCHAR2(100),
  ZHUANZHENDH    VARCHAR2(10),
  SHENQINGRQ     VARCHAR2(16),
  ZHUANZHENRQ    VARCHAR2(16),
  SHENQINGYS     VARCHAR2(10),
  ZYH            VARCHAR2(16)
);
-- Create/Recreate primary, unique and foreign key constraints 
alter table DR_CLINICRECORDLHISTORY add constraint PK_DR_CLINICRECORDLHISTORY primary key (ID);
-- Add comments to the table 
comment on table DR_CLINICRECORDLHISTORY
  is '转诊历史记录';
-- Add comments to the columns 
comment on column DR_CLINICRECORDLHISTORY.ID
  is '记录序号';
comment on column DR_CLINICRECORDLHISTORY.EMPIID
  is 'EMPIID';
comment on column DR_CLINICRECORDLHISTORY.MZHM
  is '门诊号码';
comment on column DR_CLINICRECORDLHISTORY.STATUS
  is '转诊状态  0 异常   1正常';
comment on column DR_CLINICRECORDLHISTORY.JIUZHENKLX
  is '就诊卡类型  2，社保卡，3，就诊卡';
comment on column DR_CLINICRECORDLHISTORY.JIUZHENKH
  is '就诊卡号';
comment on column DR_CLINICRECORDLHISTORY.YIBAOKLX
  is '医保卡类型';
comment on column DR_CLINICRECORDLHISTORY.YIBAOKXX
  is '医保卡信息';
comment on column DR_CLINICRECORDLHISTORY.YEWULX
  is '业务类型  0挂号1门诊转门诊  2住院转住院  3门诊转住院';
comment on column DR_CLINICRECORDLHISTORY.BINGRENXM
  is '病人姓名';
comment on column DR_CLINICRECORDLHISTORY.BINGRENXB
  is '病人性别  1男2女';
comment on column DR_CLINICRECORDLHISTORY.BINGRENCSRQ
  is '病人出生日期';
comment on column DR_CLINICRECORDLHISTORY.BINGRENNL
  is '病人年龄';
comment on column DR_CLINICRECORDLHISTORY.BINGRENSFZH
  is '病人身份证号';
comment on column DR_CLINICRECORDLHISTORY.BINGRENLXDH
  is '病人联系电话';
comment on column DR_CLINICRECORDLHISTORY.BINGRENLXDZ
  is '病人联系地址';
comment on column DR_CLINICRECORDLHISTORY.BINGRENFYLB
  is '病人费用类别';
comment on column DR_CLINICRECORDLHISTORY.SHENQINGJGDM
  is '申请机构代码';
comment on column DR_CLINICRECORDLHISTORY.SHENQINGJGMC
  is '申请机构名称';
comment on column DR_CLINICRECORDLHISTORY.SHENQINGJGLXDH
  is '申请机构联系电话';
comment on column DR_CLINICRECORDLHISTORY.SHENQINGYSDH
  is '申请医生电话';
comment on column DR_CLINICRECORDLHISTORY.ZHUANZHENYY
  is '转诊原因';
comment on column DR_CLINICRECORDLHISTORY.BINQINGMS
  is '病情描述';
comment on column DR_CLINICRECORDLHISTORY.ZHUANZHENZYSX
  is '转诊注意事项';
comment on column DR_CLINICRECORDLHISTORY.ZHUANZHENDH
  is '转诊单号';
comment on column DR_CLINICRECORDLHISTORY.SHENQINGRQ
  is '申请日期';
comment on column DR_CLINICRECORDLHISTORY.ZHUANZHENRQ
  is '转诊日期';
comment on column DR_CLINICRECORDLHISTORY.SHENQINGYS
  is '申请医生';
comment on column DR_CLINICRECORDLHISTORY.ZYH
  is '住院号';
--2015.4.15 update by gaof
ALTER TABLE JC_FYMX MODIFY FYKS NULL;

--add by yangl 
-- 公用皮试记录
create table GY_PSJL
(
  JLXH  NUMBER(18) not null,
  BRID NUMBER(18) not null,
  YPXH NUMBER(18) not null,
  JGID VARCHAR2(20) not null,
  PSJG  number(1) ,
  PSLY  number(1) ,
  GMZZ VARCHAR2(20),
  QTZZ VARCHAR2(20),
  BLFY VARCHAR2(500)
);
-- Add comments to the table 
comment on table GY_PSJL
  is '公用_病人皮试记录';
-- Add comments to the columns 
comment on column GY_PSJL.JLXH
  is '记录序号';
 comment on column GY_PSJL.BRID
  is '病人编号';
comment on column GY_PSJL.YPXH
  is '药品序号';
comment on column GY_PSJL.JGID
  is '机构代码';
 comment on column GY_PSJL.PSJG
  is '皮试结果';
comment on column GY_PSJL.GMZZ
  is '过敏症状';
comment on column GY_PSJL.QTZZ
  is '其他症状';
comment on column GY_PSJL.BLFY
  is '不良反映';
comment on column GY_PSJL.PSLY
  is '皮试来源 1:门诊 2:住院 3:家床';
-- Create/Recreate primary, unique and foreign key constraints 
alter table GY_PSJL
  add constraint PK_GY_PSJL primary key (JLXH);

--2015.4.10 update by yangl
-- Create table
create table JC_HLJL
(
  JLBH NUMBER(18) not null,
  HLRQ DATE not null,
  RHSJ DATE not null,
  CHSJ DATE not null,
  HLCS VARCHAR2(1000),
  HLHS VARCHAR2(20),
  ZYH  NUMBER(18),
  JGID VARCHAR2(20)
);
-- Add comments to the table 
comment on table JC_HLJL
  is '家床护理记录';
-- Add comments to the columns 
comment on column JC_HLJL.JLBH
  is '护理编号';
comment on column JC_HLJL.HLRQ
  is '护理日期';
comment on column JC_HLJL.RHSJ
  is '入户时间';
comment on column JC_HLJL.CHSJ
  is '出户时间';
comment on column JC_HLJL.HLCS
  is '护理措施';
comment on column JC_HLJL.HLHS
  is '护理护士';
comment on column JC_HLJL.ZYH
  is '住院号';
comment on column JC_HLJL.JGID
  is '机构id';
-- Create/Recreate primary, unique and foreign key constraints 
alter table JC_HLJL
  add constraint JC_HLJL_KEY primary key (JLBH);
-- Create table
create table JC_HLJH
(
  JLBH NUMBER(18) not null,
  KSRQ DATE not null,
  HLZD VARCHAR2(50),
  HLMB VARCHAR2(100),
  HLCS VARCHAR2(300),
  HLPJ VARCHAR2(100),
  TZRQ DATE,
  HLHS VARCHAR2(20),
  ZYH  NUMBER(18),
  JGID VARCHAR2(20)
);
-- Add comments to the table 
comment on table JC_HLJH
  is '护理计划';
-- Add comments to the columns 
comment on column JC_HLJH.JLBH
  is '计划编号';
comment on column JC_HLJH.KSRQ
  is '开始日期';
comment on column JC_HLJH.HLZD
  is '护理诊断';
comment on column JC_HLJH.HLMB
  is '护理目标';
comment on column JC_HLJH.HLCS
  is '护理措施';
comment on column JC_HLJH.HLPJ
  is '护理评价';
comment on column JC_HLJH.TZRQ
  is '停止日期';
comment on column JC_HLJH.HLHS
  is '护理护士';
comment on column JC_HLJH.ZYH
  is '住院号';
comment on column JC_HLJH.JGID
  is '机构id';
-- Create/Recreate primary, unique and foreign key constraints 
alter table JC_HLJH
  add constraint JC_HLJH_KEY primary key (JLBH);


--2015.4.8 update by yangl
-- Create table
create table JC_BRZD
(
  JLBH  NUMBER(18) not null,
  JGID  VARCHAR2(20) not null,
  ZYH  NUMBER(18) not null,
  CCXH  NUMBER(18) not null,
  BRID  NUMBER(18) not null,
  ZDLB  NUMBER(2) default 1 not null,
  ZDXH  NUMBER(18) not null,
  ICD10 VARCHAR2(20) not null,
  ZDMC  VARCHAR2(80) not null,
  PLXH  NUMBER(4),
  ZDBW  NUMBER(8),
  ZDYS  VARCHAR2(10),
  ZDSJ  DATE,
  ZGQK  NUMBER(8),
  ZGSJ  DATE
);
-- Add comments to the table 
comment on table JC_BRZD
  is '家床_病人诊断';
-- Add comments to the columns 
comment on column JC_BRZD.JLBH
  is '记录编号';
comment on column JC_BRZD.JGID
  is '机构代码';
comment on column JC_BRZD.ZYH
  is '家床号 | 家床唯一编号';
  comment on column JC_BRZD.CCXH
  is '查床序号 | 家床诊断关联查床序号';
comment on column JC_BRZD.BRID
  is '病人ID';
comment on column JC_BRZD.ZDLB
  is '诊断类别 | 1 家床诊断 2 撤床诊断';
comment on column JC_BRZD.ZDXH
  is '诊断序号';
comment on column JC_BRZD.ICD10
  is 'ICD10';
comment on column JC_BRZD.ZDMC
  is '诊断名称';
comment on column JC_BRZD.PLXH
  is '排列序号';
comment on column JC_BRZD.ZDBW
  is '诊断部位 | 部位”在字典参数中维护，如”左侧、右侧、双侧”等';
comment on column JC_BRZD.ZDYS
  is '诊断医生 | 默认当前操作者，不可修改';
comment on column JC_BRZD.ZDSJ
  is '诊断时间';
comment on column JC_BRZD.ZGQK
  is '转归情况 | 1 治愈、2 好转、3 未愈、 4 未治、5 死亡';
comment on column JC_BRZD.ZGSJ
  is '转归时间';
alter table JC_BRZD
  add constraint PK_JC_BRZD primary key (JLBH);

--2015.4.7 update by zhangyq
--alter table
alter table JC_BRRY add CCJSRQ DATE;
comment on column JC_BRRY.CCJSRQ
  is '撤床结算日期';
-- Create table
create table JC_JCJS
(
  ZYH  NUMBER(18) default 0 not null,
  JSCS NUMBER(3) default 1 not null,
  JGID VARCHAR2(20) default 1 not null,
  JSLX NUMBER(2) default 1 not null,
  KSRQ DATE,
  ZZRQ DATE,
  JSRQ DATE not null,
  FYHJ NUMBER(10,2) default 0 not null,
  ZFHJ NUMBER(10,2) default 0 not null,
  JKHJ NUMBER(10,2) default 0 not null,
  FPHM VARCHAR2(20),
  CZGH VARCHAR2(10),
  JZRQ DATE,
  HZRQ DATE,
  TPHM VARCHAR2(8),
  ZFRQ DATE,
  ZFGH VARCHAR2(10),
  ZFPB NUMBER(1) default 0 not null,
  JSXM VARCHAR2(250),
  JSJK VARCHAR2(250),
  BRXZ NUMBER(18) not null
);
-- Add comments to the table 
comment on table JC_JCJS
  is '住院_住院结算';
-- Add comments to the columns 
comment on column JC_JCJS.ZYH
  is '住院号';
comment on column JC_JCJS.JSCS
  is '结算次数';
comment on column JC_JCJS.JGID
  is '机构代码';
comment on column JC_JCJS.JSLX
  is '结算类型 | 1：中途结算
2：预结（不写JC_JCJS）
3：预结后出院结算
4：终结处理(异常出院)
5：正常出院结算
6：合并结算
9：退费
';
comment on column JC_JCJS.KSRQ
  is '开始日期 | 结算费用的开始日期

';
comment on column JC_JCJS.ZZRQ
  is '终止日期 | 结算费用的终止日期
';
comment on column JC_JCJS.JSRQ
  is '结算日期 | 结算时服务器时间';
comment on column JC_JCJS.FYHJ
  is '费用合计 | 本次结算费用合计(包含记帐部分费用)';
comment on column JC_JCJS.ZFHJ
  is '自负合计 | 本次结算费用中自负费用总计';
comment on column JC_JCJS.JKHJ
  is '缴款合计 | 从JC_TBKK表中统计';
comment on column JC_JCJS.FPHM
  is '发票号码 | 病人结算时打印的结算发票号码';
comment on column JC_JCJS.CZGH
  is '操作工号';
comment on column JC_JCJS.JZRQ
  is '结账日期';
comment on column JC_JCJS.HZRQ
  is '汇总日期 | 同JC_TBKK表中HZRQ住院处做汇总日期时填写';
comment on column JC_JCJS.TPHM
  is '退票号码 | 病人办理补退费时所退的原结算发票号码';
comment on column JC_JCJS.ZFRQ
  is '作废日期';
comment on column JC_JCJS.ZFGH
  is '作废工号';
comment on column JC_JCJS.ZFPB
  is '作废判别';
comment on column JC_JCJS.JSXM
  is '结算项目 | 中途结算时选择的费用项目';
comment on column JC_JCJS.JSJK
  is '结算缴款 | 结算时被冲消的缴款序号';
comment on column JC_JCJS.BRXZ
  is '病人性质 | 病人结算时的性质';
-- Create/Recreate primary, unique and foreign key constraints 
alter table JC_JCJS
  add constraint PK_JC_JCJS primary key (ZYH, JSCS);
alter table JC_JCJS
  add constraint FK_JC_JCJS_REF_4067_JC_BRRY foreign key (ZYH)
  references JC_BRRY (ZYH);
-- Create/Recreate check constraints 
alter table JC_JCJS
  add constraint CKC_ZFPB_JC_JCJS
  check (ZFPB in (0,1));
-- Create/Recreate indexes 
create index IDX_JC_JCJS_JSRQ on JC_JCJS (JSRQ);
create index IDX_JC_JCJS_ZYH on JC_JCJS (ZYH);

-- Create table
create table JC_JZXX
(
  JZRQ   DATE not null,
  CZGH   VARCHAR2(10) not null,
  JGID   VARCHAR2(20) default 1 not null,
  CYSR   NUMBER(12,2) default 0 not null,
  YJJE   NUMBER(12,2) default 0 not null,
  YJXJ   NUMBER(12,2) default 0 not null,
  YJZP   NUMBER(12,2) default 0 not null,
  TPJE   NUMBER(12,2) default 0 not null,
  FPZS   NUMBER(4) default 0 not null,
  SJZS   NUMBER(4) default 0 not null,
  YSJE   NUMBER(12,2) default 0 not null,
  YSXJ   NUMBER(12,2) default 0 not null,
  YSZP   NUMBER(12,2) default 0 not null,
  ZPZS   NUMBER(4) default 0 not null,
  TYJJ   NUMBER(12,2) default 0 not null,
  TJKS   NUMBER(4) default 0 not null,
  KBJE   NUMBER(12,2) default 0 not null,
  KBZP   NUMBER(4) default 0 not null,
  HZRQ   DATE,
  YJQT   NUMBER(12,2) default 0 not null,
  YSQT   NUMBER(12,2) default 0 not null,
  QTZS   NUMBER(4) default 0 not null,
  SRJE   NUMBER(6,2) default 0 not null,
  YJYHK  NUMBER(12,2) default 0 not null,
  YSYHK  NUMBER(12,2) default 0 not null,
  YSYH   NUMBER(12,2) default 0 not null,
  QZPJ   VARCHAR2(255),
  QZSJ   VARCHAR2(255),
  ZXJZFY NUMBER(6),
  GRXJZF NUMBER(6),
  BCZHZF NUMBER(6),
  TCZC   NUMBER(6),
  DBZC   NUMBER(6),
  AZQGFY NUMBER(6),
  QTYSFB VARCHAR2(1000)
);
-- Add comments to the table 
comment on table JC_JZXX
  is '家床_收款结帐信息';
-- Add comments to the columns 
comment on column JC_JZXX.JZRQ
  is '结算日期 | 权责发生制所有收款员日报汇总日期';
comment on column JC_JZXX.CZGH
  is '操作工号';
comment on column JC_JZXX.JGID
  is '机构代码';
comment on column JC_JZXX.CYSR
  is '出院收入';
comment on column JC_JZXX.YJJE
  is '预交金额';
comment on column JC_JZXX.YJXJ
  is '预交现金';
comment on column JC_JZXX.YJZP
  is '预交支票';
comment on column JC_JZXX.TPJE
  is '退票金额';
comment on column JC_JZXX.FPZS
  is '发票张数';
comment on column JC_JZXX.SJZS
  is '收据张数';
comment on column JC_JZXX.YSJE
  is '应收金额';
comment on column JC_JZXX.YSXJ
  is '应收现金';
comment on column JC_JZXX.YSZP
  is '应收支票';
comment on column JC_JZXX.ZPZS
  is '支票张数';
comment on column JC_JZXX.TYJJ
  is '退预交金 | 同JC_JZXX.YSJE';
comment on column JC_JZXX.TJKS
  is '退预缴款张数';
comment on column JC_JZXX.KBJE
  is '空白支票金额';
comment on column JC_JZXX.KBZP
  is '空白支票张数';
comment on column JC_JZXX.HZRQ
  is '汇总日期';
comment on column JC_JZXX.YJQT
  is '预缴其他';
comment on column JC_JZXX.YSQT
  is '应收其他';
comment on column JC_JZXX.QTZS
  is '其他票据张数';
comment on column JC_JZXX.SRJE
  is '舍入金额 | 同ZY_ZYJS.SRJE';
comment on column JC_JZXX.YJYHK
  is '预缴银行卡';
comment on column JC_JZXX.YSYHK
  is '应收银行卡';
comment on column JC_JZXX.YSYH
  is '应收优惠';
comment on column JC_JZXX.QZPJ
  is '起至票据';
comment on column JC_JZXX.QZSJ
  is '起至收据';
comment on column JC_JZXX.QTYSFB
  is '收费类型统计';
-- Create/Recreate primary, unique and foreign key constraints 
alter table JC_JZXX
  add constraint PK_JC_JZXX primary key (JZRQ, CZGH);

-- Create table
create table JC_ZFPJ
(
  JZRQ DATE not null,
  CZGH VARCHAR2(10) not null,
  PJLB NUMBER(2) default 1 not null,
  PJHM VARCHAR2(8) not null,
  JGID VARCHAR2(20) default 1 not null
);
-- Add comments to the table 
comment on table JC_ZFPJ
  is '住院_作废票据';
-- Add comments to the columns 
comment on column JC_ZFPJ.JZRQ
  is '结帐日期';
comment on column JC_ZFPJ.CZGH
  is '操作工号';
comment on column JC_ZFPJ.PJLB
  is '票据类别 | 1.发票  2.收据 3.退票';
comment on column JC_ZFPJ.PJHM
  is '票据号码';
comment on column JC_ZFPJ.JGID
  is '机构ID';
-- Create/Recreate primary, unique and foreign key constraints 
alter table JC_ZFPJ
  add constraint PK_JC_ZFPJ primary key (JZRQ, CZGH, PJLB, PJHM);
alter table JC_ZFPJ
  add constraint FK_JC_ZFPJ_REF_11559_JC_JZXX foreign key (JZRQ, CZGH)
  references JC_JZXX (JZRQ, CZGH);


-- Create table
create table JC_JSZF
(
  ZYH  NUMBER(18) not null,
  JSCS NUMBER(3) not null,
  JGID VARCHAR2(20) default 1 not null,
  ZFGH VARCHAR2(10) not null,
  ZFRQ DATE not null,
  JZRQ DATE,
  HZRQ DATE
);
-- Add comments to the table 
comment on table JC_JSZF
  is '住院_结算作废';
-- Add comments to the columns 
comment on column JC_JSZF.ZYH
  is '住院号';
comment on column JC_JSZF.JSCS
  is '结算次数';
comment on column JC_JSZF.JGID
  is '机构代码';
comment on column JC_JSZF.ZFGH
  is '作废工号';
comment on column JC_JSZF.ZFRQ
  is '作废日期';
comment on column JC_JSZF.JZRQ
  is '结账日期';
comment on column JC_JSZF.HZRQ
  is '汇总日期';
-- Create/Recreate primary, unique and foreign key constraints 
alter table JC_JSZF
  add constraint PK_JC_JSZF primary key (ZYH, JSCS);
alter table JC_JSZF
  add constraint FK_JC_JSZF_REF_27871_JC_JCJS foreign key (ZYH, JSCS)
  references JC_JCJS (ZYH, JSCS);


-- Create table
create table JC_FKXX
(
  JLXH NUMBER(18) not null,
  ZYH  NUMBER(18) default 0 not null,
  JSCS NUMBER(3) default 1 not null,
  JGID VARCHAR2(20) default 1 not null,
  FKFS NUMBER(18) not null,
  FKJE NUMBER(12,2) default 0 not null,
  FKHM VARCHAR2(40)
);
-- Add comments to the table 
comment on table JC_FKXX
  is '住院_结算付款信息';
-- Add comments to the columns 
comment on column JC_FKXX.JLXH
  is '记录序号';
comment on column JC_FKXX.ZYH
  is '住院号';
comment on column JC_FKXX.JSCS
  is '结算次数';
comment on column JC_FKXX.JGID
  is '机构代码';
comment on column JC_FKXX.FKFS
  is '付款方式';
comment on column JC_FKXX.FKJE
  is '付款金额';
comment on column JC_FKXX.FKHM
  is '付款号码';
-- Create/Recreate primary, unique and foreign key constraints 
alter table JC_FKXX
  add constraint PK_JC_FKXX primary key (JLXH);
alter table JC_FKXX
  add constraint FK_JC_FKXX_REFERENCE_JC_JCJS foreign key (ZYH, JSCS)
  references JC_JCJS (ZYH, JSCS);
-- Create/Recreate indexes 
create index IDX_JC_FKXX_ZYH_JSCS on JC_FKXX (ZYH, JSCS);


-- Create table
create table JC_FYMX_JS
(
  JLXH NUMBER(18) not null,
  JGID VARCHAR2(20) default 1 not null,
  ZYH  NUMBER(18) not null,
  FYRQ DATE not null,
  FYXH NUMBER(18) not null,
  FYMC VARCHAR2(80),
  YPCD NUMBER(18) default 0 not null,
  FYSL NUMBER(10,2) default 0 not null,
  FYDJ NUMBER(10,4) default 0 not null,
  ZJJE NUMBER(12,2) default 0 not null,
  ZFJE NUMBER(12,2) default 0 not null,
  YSGH VARCHAR2(10),
  SRGH VARCHAR2(10),
  QRGH VARCHAR2(10),
  FYBQ NUMBER(18),
  FYKS NUMBER(18),
  ZXKS NUMBER(18),
  JFRQ DATE not null,
  XMLX NUMBER(2) not null,
  YPLX NUMBER(1) default 0 not null,
  FYXM NUMBER(18) not null,
  JSCS NUMBER(3) default 0 not null,
  ZFBL NUMBER(4,3) default 1 not null,
  YZXH NUMBER(18),
  HZRQ DATE,
  YJRQ VARCHAR2(8),
  ZLJE NUMBER(12,2) default 0 not null,
  ZLXZ NUMBER(4),
  YEPB NUMBER(1) default 0,
  DZBL NUMBER(6,3) default 0 not null
);
-- Add comments to the table 
comment on table JC_FYMX_JS
  is '住院_结算费用明细 | 在住院结算后产生该表';
-- Add comments to the columns 
comment on column JC_FYMX_JS.JLXH
  is '记录序号';
comment on column JC_FYMX_JS.JGID
  is '机构代码';
comment on column JC_FYMX_JS.ZYH
  is '住院号';
comment on column JC_FYMX_JS.FYRQ
  is '费用日期';
comment on column JC_FYMX_JS.FYXH
  is '费用序号';
comment on column JC_FYMX_JS.FYMC
  is '费用名称';
comment on column JC_FYMX_JS.YPCD
  is '药品产地';
comment on column JC_FYMX_JS.FYSL
  is '费用数量';
comment on column JC_FYMX_JS.FYDJ
  is '费用单价';
comment on column JC_FYMX_JS.ZJJE
  is '总计金额';
comment on column JC_FYMX_JS.ZFJE
  is '自负金额';
comment on column JC_FYMX_JS.YSGH
  is '医生工号';
comment on column JC_FYMX_JS.SRGH
  is '输入工号';
comment on column JC_FYMX_JS.QRGH
  is '确认工号';
comment on column JC_FYMX_JS.FYBQ
  is '费用病区';
comment on column JC_FYMX_JS.FYKS
  is '费用科室';
comment on column JC_FYMX_JS.ZXKS
  is '执行科室';
comment on column JC_FYMX_JS.JFRQ
  is '计费日期';
comment on column JC_FYMX_JS.XMLX
  is '项目类型 | 1：病区系统记帐
2：药房系统记帐
3：医技系统记帐
4：住院系统记帐
5：手术麻醉记帐
9：自动累加费用
';
comment on column JC_FYMX_JS.YPLX
  is '药品类型 | 0：费用
1：西药
2：中成药
3：中草药
';
comment on column JC_FYMX_JS.FYXM
  is '费用项目 | 指定FYXH归并的项目(同GY_SFMX表中SFXM对应)';
comment on column JC_FYMX_JS.JSCS
  is '结算次数';
comment on column JC_FYMX_JS.ZFBL
  is '自负比例 | 同GY_ZFBL或 GY_YPJY,GY_FYJY表中自负比例对应';
comment on column JC_FYMX_JS.YZXH
  is '医嘱序号';
comment on column JC_FYMX_JS.HZRQ
  is '汇总日期';
comment on column JC_FYMX_JS.YJRQ
  is '月结日期';
comment on column JC_FYMX_JS.ZLJE
  is '自理金额';
comment on column JC_FYMX_JS.ZLXZ
  is '医疗小组';
comment on column JC_FYMX_JS.YEPB
  is '婴儿判别 | 0 大人  1 小孩 ';
comment on column JC_FYMX_JS.DZBL
  is '打折比例';
-- Create/Recreate primary, unique and foreign key constraints 
alter table JC_FYMX_JS
  add constraint PK_JC_FYMX_JS primary key (JLXH, JSCS);
-- Create/Recreate indexes 
create index IDX_JC_FYMX_JS_FYRQ on JC_FYMX_JS (FYRQ);
create index IDX_JC_FYMX_JS_JZRQ on JC_FYMX_JS (HZRQ);
create index IDX_JC_FYMX_JS_ZYH_JSCS on JC_FYMX_JS (ZYH, JSCS);


-- Create table
create table JC_JSMX
(
  ZYH  NUMBER(18) default 0 not null,
  JSCS NUMBER(3) default 1 not null,
  KSDM NUMBER(18),
  FYXM NUMBER(18) not null,
  JGID VARCHAR2(20) default 1 not null,
  ZJJE NUMBER(12,2) default 0 not null,
  ZFJE NUMBER(12,2) default 0 not null,
  ZLJE NUMBER(12,2) default 0 not null
);
-- Add comments to the table 
comment on table JC_JSMX
  is '住院_结算明细';
-- Add comments to the columns 
comment on column JC_JSMX.ZYH
  is '住院号';
comment on column JC_JSMX.JSCS
  is '结算次数';
comment on column JC_JSMX.KSDM
  is '科室代码';
comment on column JC_JSMX.FYXM
  is '费用项目';
comment on column JC_JSMX.JGID
  is '机构代码';
comment on column JC_JSMX.ZJJE
  is '总结金额';
comment on column JC_JSMX.ZFJE
  is '自负金额';
comment on column JC_JSMX.ZLJE
  is '自理金额';
-- Create/Recreate primary, unique and foreign key constraints 
alter table JC_JSMX
  add constraint PK_JC_JSMX primary key (ZYH, JSCS, FYXM);
alter table JC_JSMX
  add constraint FK_JC_JSMX_REF_16649_JC_JCJS foreign key (ZYH, JSCS)
  references JC_JCJS (ZYH, JSCS);


-- Create table
create table JC_FYHZ
(
  JGID VARCHAR2(20) not null,
  HZRQ DATE not null,
  FYXM NUMBER(18) not null,
  SQJC NUMBER(12,2) default 0 not null,
  BQFS NUMBER(12,2) default 0 not null,
  BQJS NUMBER(12,2) default 0 not null,
  BQJC NUMBER(12,2) default 0 not null,
  SJJC NUMBER(12,2) default 0 not null
);
-- Add comments to the table 
comment on table JC_FYHZ
  is '费用汇总';
-- Add comments to the columns 
comment on column JC_FYHZ.JGID
  is '机构ID';
comment on column JC_FYHZ.HZRQ
  is '汇总日期';
comment on column JC_FYHZ.FYXM
  is '费用项目';
comment on column JC_FYHZ.SQJC
  is '上期结存';
comment on column JC_FYHZ.BQFS
  is '本期发生';
comment on column JC_FYHZ.BQJS
  is '本期结算';
comment on column JC_FYHZ.BQJC
  is '本期结存';
comment on column JC_FYHZ.SJJC
  is '实际结存';
-- Create/Recreate primary, unique and foreign key constraints 
alter table JC_FYHZ
  add constraint PK_JC_FYHZ primary key (JGID, HZRQ, FYXM);

  
  -- Create table
create table JC_SRHZ
(
  JGID VARCHAR2(20) not null,
  HZRQ DATE not null,
  KSLB NUMBER(1),
  KSDM NUMBER(18),
  SFXM NUMBER(18) not null,
  ZJJE NUMBER(12,2) default 0 not null,
  ZFJE NUMBER(12,2) default 0 not null
);
-- Add comments to the table 
comment on table JC_SRHZ
  is '住院收入汇总';
-- Add comments to the columns 
comment on column JC_SRHZ.JGID
  is '机构ID';
comment on column JC_SRHZ.HZRQ
  is '汇总日期';
comment on column JC_SRHZ.KSLB
  is '科室类别 | 1.科室  2.病区';
comment on column JC_SRHZ.KSDM
  is '科室代码';
comment on column JC_SRHZ.SFXM
  is '收费项目';
comment on column JC_SRHZ.ZJJE
  is '总计金额';
comment on column JC_SRHZ.ZFJE
  is '自负金额';
-- Create/Recreate primary, unique and foreign key constraints 
alter table JC_SRHZ
  add constraint PK_JC_SRHZ primary key (JGID, HZRQ, SFXM);
  
  
  -- Create table
create table JC_JZHZ
(
  JGID VARCHAR2(20) not null,
  HZRQ DATE not null,
  XMBH NUMBER(1) default 1 not null,
  SQJC NUMBER(12,2) default 0 not null,
  BQFS NUMBER(12,2) default 0 not null,
  BQJS NUMBER(12,2) default 0 not null,
  XJZP NUMBER(12,2) default 0 not null,
  YHJE NUMBER(12,2) default 0 not null,
  CYDJ NUMBER(12,2) default 0 not null,
  QFJE NUMBER(12,2) default 0 not null,
  CBJE NUMBER(12,2) default 0 not null,
  QTJE NUMBER(12,2) default 0 not null,
  BQYE NUMBER(12,2) default 0 not null
);
-- Add comments to the table 
comment on table JC_JZHZ
  is '住院_收入结帐汇总';
-- Add comments to the columns 
comment on column JC_JZHZ.JGID
  is '机构ID';
comment on column JC_JZHZ.HZRQ
  is '汇总日期';
comment on column JC_JZHZ.XMBH
  is '项目编号 | 1.在院病人结算  2.预缴金  3.出院待结算';
comment on column JC_JZHZ.SQJC
  is '上期结存';
comment on column JC_JZHZ.BQFS
  is '本期发生';
comment on column JC_JZHZ.BQJS
  is '本期结算';
comment on column JC_JZHZ.XJZP
  is '现金支票';
comment on column JC_JZHZ.YHJE
  is '优惠金额';
comment on column JC_JZHZ.CYDJ
  is '出院待结';
comment on column JC_JZHZ.QFJE
  is '欠费金额';
comment on column JC_JZHZ.CBJE
  is '参保应收';
comment on column JC_JZHZ.QTJE
  is '其他应收';
comment on column JC_JZHZ.BQYE
  is '本期余额';
-- Create/Recreate primary, unique and foreign key constraints 
alter table JC_JZHZ
  add constraint PK_JC_JZHZ primary key (JGID, HZRQ, XMBH);
  
  
  -- Create table
create table JC_JKZF
(
  JKXH NUMBER(18) not null,
  JGID VARCHAR2(20) default 1 not null,
  ZFGH VARCHAR2(10) not null,
  ZFRQ DATE not null,
  JZRQ DATE,
  HZRQ DATE
);
-- Add comments to the table 
comment on table JC_JKZF
  is '住院_缴款作废';
-- Add comments to the columns 
comment on column JC_JKZF.JKXH
  is '缴款序号';
comment on column JC_JKZF.JGID
  is '机构代码';
comment on column JC_JKZF.ZFGH
  is '作废工号';
comment on column JC_JKZF.ZFRQ
  is '作废日期';
comment on column JC_JKZF.JZRQ
  is '结账日期';
comment on column JC_JKZF.HZRQ
  is '汇总日期';
-- Create/Recreate primary, unique and foreign key constraints 
alter table JC_JKZF
  add constraint PK_JC_JKZF primary key (JKXH);
alter table JC_JKZF
  add constraint FK_JC_JKZF_REF_27885_JC_TBKK foreign key (JKXH)
  references JC_TBKK (JKXH);
--2015.4.3 update by gaof 
alter table JC_BRRY add JCBH varchar(30);
comment on column JC_BRRY.JCBH  is '家床编号';

--2015.3.20 update by gaof 
-- Create table
create table JC_CCJL
(
  SBXH NUMBER(18) not null,
  ZYH  NUMBER(18),
  CCSJ DATE,
  ZSXX VARCHAR2(1000),
  TGJC VARCHAR2(1000),
  FZJC VARCHAR2(1000),
  GTQK VARCHAR2(1000),
  XTSJ VARCHAR2(1000),
  YSDM VARCHAR2(20)
);
-- Add comments to the table 
comment on table JC_CCJL
  is '家床查床记录表';
-- Add comments to the columns 
comment on column JC_CCJL.SBXH
  is '主键';
comment on column JC_CCJL.ZYH
  is '家床编号 | JC_BRRY主键';
comment on column JC_CCJL.CCSJ
  is '查床时间';
comment on column JC_CCJL.ZSXX
  is '主诉信息';
comment on column JC_CCJL.TGJC
  is '体格检查';
comment on column JC_CCJL.FZJC
  is '辅助检查';
comment on column JC_CCJL.GTQK
  is '联系人沟通情况';
comment on column JC_CCJL.XTSJ
  is '系统时间';
comment on column JC_CCJL.YSDM
  is '医生代码';
-- Create/Recreate primary, unique and foreign key constraints 
alter table JC_CCJL
  add constraint PK_JC_CCJL_SBXH primary key (SBXH)
  using index;
 
-- Create table
create table JC_BRYZ
(
  JLXH   NUMBER(18) not null,
  JGID   VARCHAR2(20) not null,
  ZYH    NUMBER(18) not null,
  YZMC   VARCHAR2(100),
  YPXH   NUMBER(18) not null,
  YPCD   NUMBER(18) default 0 not null,
  XMLX   NUMBER(2) not null,
  YPLX   NUMBER(1) not null,
  MRCS   NUMBER(2) default 1 not null,
  YCJL   NUMBER(10,3) default 0 not null,
  YCSL   NUMBER(8,4) default 0 not null,
  MZCS   NUMBER(1) default 0 not null,
  KSSJ   DATE,
  QRSJ   DATE,
  TZSJ   DATE,
  YPDJ   NUMBER(12,4) default 0 not null,
  YPYF   NUMBER(18) default 0 not null,
  YSGH   VARCHAR2(20),
  TZYS   VARCHAR2(20),
  CZGH   VARCHAR2(20),
  FHGH   VARCHAR2(20),
  SYBZ   NUMBER(1) default 0 not null,
  SRKS   NUMBER(18),
  ZFPB   NUMBER(1) default 0 not null,
  YJZX   NUMBER(1) default 0 not null,
  YJXH   NUMBER(18) default 0 not null,
  TJHM   VARCHAR2(10),
  ZXKS   NUMBER(18),
  APRQ   DATE,
  YZZH   NUMBER(18) not null,
  SYPC   VARCHAR2(6),
  FYSX   NUMBER(1) default 0 not null,
  YEPB   NUMBER(1) default 0 not null,
  YFSB   NUMBER(18) default 0 not null,
  LSYZ   NUMBER(1) default 0 not null,
  LSBZ   NUMBER(1) default 0 not null,
  YZPB   NUMBER(1) default 0 not null,
  JFBZ   NUMBER(1) default 1 not null,
  BZXX   VARCHAR2(250),
  HYXM   VARCHAR2(250),
  FYFS   NUMBER(18),
  TPN    NUMBER(1) default 0 not null,
  YSBZ   NUMBER(1) default 0 not null,
  YSTJ   NUMBER(1) default 0 not null,
  FYTX   DATE,
  YZPX   NUMBER(2),
  SQWH   NUMBER(18),
  YSYZBH NUMBER(18),
  SQID   NUMBER(18),
  ZFBZ   NUMBER(1) default 0 not null,
  XML    VARCHAR2(1024),
  SQDMC  VARCHAR2(40),
  SSBH   NUMBER(18),
  YEWYH  NUMBER(18) default 0,
  SRCS   NUMBER(6),
  PZPC   VARCHAR2(255),
  SFJG   INTEGER default 0 not null,
  YYTS   NUMBER(4),
  YFGG   VARCHAR2(20),
  YFDW   VARCHAR2(4),
  YFBZ   NUMBER(4),
  BRKS   NUMBER(18),
  BRBQ   NUMBER(18),
  BRCH   VARCHAR2(12),
  YZZXSJ VARCHAR2(150),
  FHBZ   NUMBER(8) default 0 not null,
  FHSJ   DATE,
  TZFHBZ NUMBER(8) not null,
  TZFHR  VARCHAR2(10) default '0',
  TZFHSJ DATE,
  SFGH   VARCHAR2(10),
  SFYJ   VARCHAR2(255),
  PSPB   NUMBER(1) default 0 not null,
  PSJG   NUMBER(2),
  PSSJ   DATE,
  PSGH   VARCHAR2(10),
  YYPS   NUMBER(1) default 2,
  PSGL   NUMBER(18),
  PSFH   VARCHAR2(10),
  CFTS   NUMBER(8),
  YPZS   NUMBER(1),
  JZ     NUMBER(1),
  YQSY   NUMBER(1),
  DYZT   NUMBER(1) default 0,
  YDLB   VARCHAR2(10),
  YWID   NUMBER(18),
  ZFYP   NUMBER(1) default 0,
  TJZRQ  VARCHAR2(10),
  CCJL   NUMBER(18),
  HSGH   VARCHAR2(20),
  TJZX   NUMBER(1),
  TZCCJL NUMBER(18)
);
-- Add comments to the table 
comment on table JC_BRYZ
  is ' | 此表参考住院病区医嘱表，部分字段预留';
-- Add comments to the columns 
comment on column JC_BRYZ.JLXH
  is '记录序号';
comment on column JC_BRYZ.JGID
  is '机构代码';
comment on column JC_BRYZ.ZYH
  is '住院号';
comment on column JC_BRYZ.YZMC
  is '药嘱名称';
comment on column JC_BRYZ.YPXH
  is '药品序号';
comment on column JC_BRYZ.YPCD
  is '药品产地';
comment on column JC_BRYZ.XMLX
  is '项目类型';
comment on column JC_BRYZ.YPLX
  is '药品类型';
comment on column JC_BRYZ.MRCS
  is '每日次数';
comment on column JC_BRYZ.YCJL
  is '一次剂量';
comment on column JC_BRYZ.YCSL
  is '一次数量';
comment on column JC_BRYZ.MZCS
  is '每周次数';
comment on column JC_BRYZ.KSSJ
  is '开始时间';
comment on column JC_BRYZ.QRSJ
  is '确认时间';
comment on column JC_BRYZ.TZSJ
  is '停止时间';
comment on column JC_BRYZ.YPDJ
  is '药品单价';
comment on column JC_BRYZ.YPYF
  is '药品用法';
comment on column JC_BRYZ.YSGH
  is '开嘱医生';
comment on column JC_BRYZ.TZYS
  is '停嘱医生';
comment on column JC_BRYZ.CZGH
  is '操作工号';
comment on column JC_BRYZ.FHGH
  is '复核工号';
comment on column JC_BRYZ.SYBZ
  is '使用标志';
comment on column JC_BRYZ.SRKS
  is '输入科室';
comment on column JC_BRYZ.ZFPB
  is '自负判别';
comment on column JC_BRYZ.YJZX
  is '医技主项';
comment on column JC_BRYZ.YJXH
  is '医技序号';
comment on column JC_BRYZ.TJHM
  is '特检号码';
comment on column JC_BRYZ.ZXKS
  is '执行科室';
comment on column JC_BRYZ.APRQ
  is '安排日期';
comment on column JC_BRYZ.YZZH
  is '医嘱组号';
comment on column JC_BRYZ.SYPC
  is '使用频次';
comment on column JC_BRYZ.FYSX
  is '发药属性';
comment on column JC_BRYZ.YEPB
  is '婴儿判别';
comment on column JC_BRYZ.YFSB
  is '药房识别';
comment on column JC_BRYZ.LSYZ
  is '临时医嘱';
comment on column JC_BRYZ.LSBZ
  is '历史标志';
comment on column JC_BRYZ.YZPB
  is '医嘱判别';
comment on column JC_BRYZ.JFBZ
  is '计费标志';
comment on column JC_BRYZ.BZXX
  is '备注';
comment on column JC_BRYZ.HYXM
  is '化验项目';
comment on column JC_BRYZ.FYFS
  is '发药方式';
comment on column JC_BRYZ.TPN
  is 'TPN';
comment on column JC_BRYZ.YSBZ
  is '医生医嘱标志';
comment on column JC_BRYZ.YSTJ
  is '医生提交标志';
comment on column JC_BRYZ.FYTX
  is '发药提醒';
comment on column JC_BRYZ.YZPX
  is '医嘱排序';
comment on column JC_BRYZ.SQWH
  is '申请文号';
comment on column JC_BRYZ.YSYZBH
  is '医生医嘱编号';
comment on column JC_BRYZ.SQID
  is '申请ID';
comment on column JC_BRYZ.ZFBZ
  is '作废标志';
comment on column JC_BRYZ.XML
  is '申请单XML';
comment on column JC_BRYZ.SQDMC
  is '申请单名称';
comment on column JC_BRYZ.SSBH
  is '手术编号';
comment on column JC_BRYZ.YEWYH
  is '婴儿唯一号';
comment on column JC_BRYZ.SRCS
  is '首日次数';
comment on column JC_BRYZ.PZPC
  is '配置批次';
comment on column JC_BRYZ.SFJG
  is '审方结果';
comment on column JC_BRYZ.YYTS
  is '用药天数';
comment on column JC_BRYZ.YFGG
  is '药房规格';
comment on column JC_BRYZ.YFDW
  is '药房单位';
comment on column JC_BRYZ.YFBZ
  is '药房包装';
comment on column JC_BRYZ.BRKS
  is '病人科室';
comment on column JC_BRYZ.BRBQ
  is '病人病区';
comment on column JC_BRYZ.BRCH
  is '病人床号';
comment on column JC_BRYZ.YZZXSJ
  is '医嘱执行时间';
comment on column JC_BRYZ.FHBZ
  is '复核标志';
comment on column JC_BRYZ.FHSJ
  is '复核时间';
comment on column JC_BRYZ.TZFHBZ
  is '停嘱复核标志';
comment on column JC_BRYZ.TZFHR
  is '停嘱复核人';
comment on column JC_BRYZ.TZFHSJ
  is '停嘱复核时间';
comment on column JC_BRYZ.SFGH
  is '审方工号';
comment on column JC_BRYZ.SFYJ
  is '审方意见';
comment on column JC_BRYZ.PSPB
  is '皮试判别';
comment on column JC_BRYZ.PSJG
  is '皮试结果';
comment on column JC_BRYZ.PSSJ
  is '皮试时间';
comment on column JC_BRYZ.PSGH
  is '皮试工号';
comment on column JC_BRYZ.YYPS
  is '原液皮试';
comment on column JC_BRYZ.PSGL
  is '皮试关联';
comment on column JC_BRYZ.PSFH
  is '皮试复核';
comment on column JC_BRYZ.CFTS
  is '贴数';
comment on column JC_BRYZ.YPZS
  is '煎法';
comment on column JC_BRYZ.JZ
  is '脚注';
comment on column JC_BRYZ.YQSY
  is '越权使用标志';
comment on column JC_BRYZ.DYZT
  is '打印状态,0未打印,1医嘱打印,2停嘱打印';
comment on column JC_BRYZ.YDLB
  is '约定类别';
comment on column JC_BRYZ.YWID
  is '补充业务ID';
comment on column JC_BRYZ.ZFYP
  is '自备药标识';
comment on column JC_BRYZ.TJZRQ
  is '提交至日期 | 长期医嘱使用';
comment on column JC_BRYZ.CCJL
  is '查床记录关联字段 | 开嘱时对应的序号';
comment on column JC_BRYZ.HSGH
  is '护士工号';
comment on column JC_BRYZ.TJZX
  is '提交执行,1是提交 ,2是执行(或发药)';
comment on column JC_BRYZ.TZCCJL
  is '停嘱的查床序号';
-- Create/Recreate primary, unique and foreign key constraints 
alter table JC_BRYZ
  add constraint PK_BRYZ primary key (JLXH)
  using index;

-- Create table
create table JC_YPGM
(
  ZYH  NUMBER(18) not null,
  YPXH NUMBER(18) not null,
  JGID VARCHAR2(20) not null,
  GMZZ VARCHAR2(20),
  QTZZ VARCHAR2(20),
  BLFY VARCHAR2(500)
);
-- Add comments to the table 
comment on table JC_YPGM
  is '住院_病人过敏药物';
-- Add comments to the columns 
comment on column JC_YPGM.ZYH
  is '住院号';
comment on column JC_YPGM.YPXH
  is '药品序号';
comment on column JC_YPGM.JGID
  is '机构代码';
comment on column JC_YPGM.GMZZ
  is '过敏症状';
comment on column JC_YPGM.QTZZ
  is '其他症状';
comment on column JC_YPGM.BLFY
  is '不良反映';
-- Create/Recreate primary, unique and foreign key constraints 
alter table JC_YPGM
  add constraint PK_JC_YPGM primary key (ZYH, YPXH)
  using index;

-- Create table
create table JC_ZLJH
(
  JLBH NUMBER(18) not null,
  ZYH  NUMBER(18) not null,
  YPZH NUMBER(3),
  XMBH NUMBER(18) default 0 not null,
  XMMC VARCHAR2(100) not null,
  XMLX NUMBER(1) not null,
  YSDM VARCHAR2(50),
  YCJL NUMBER(10,3),
  SYPC VARCHAR2(12),
  GYTJ NUMBER(9),
  KSSJ DATE,
  JSSJ DATE,
  BZXX VARCHAR2(200),
  JGID VARCHAR2(20)
);
-- Add comments to the columns 
comment on column JC_ZLJH.JLBH
  is '记录编码';
comment on column JC_ZLJH.ZYH
  is '家床号 | 关联JC_BRRY';
comment on column JC_ZLJH.YPZH
  is '家床诊疗计划明细组号';
comment on column JC_ZLJH.XMBH
  is '诊疗项目编号 | 行为医嘱默认为0';
comment on column JC_ZLJH.XMMC
  is '诊疗项目名称';
comment on column JC_ZLJH.XMLX
  is '项目类型  | 1：药品 2：项目 3：行为';
comment on column JC_ZLJH.YSDM
  is '计划医生';
comment on column JC_ZLJH.YCJL
  is '一次剂量';
comment on column JC_ZLJH.SYPC
  is '使用频次';
comment on column JC_ZLJH.GYTJ
  is '给药途径';
comment on column JC_ZLJH.KSSJ
  is '开始日期';
comment on column JC_ZLJH.JSSJ
  is '结束日期';
comment on column JC_ZLJH.BZXX
  is '备注信息';
comment on column JC_ZLJH.JGID
  is '所属机构';
-- Create/Recreate primary, unique and foreign key constraints 
alter table JC_ZLJH
  add constraint PK_ZLJH primary key (JLBH)
  using index;

-- Create table
create table JC_ZL_ZT01
(
  ZTBH NUMBER(18) not null,
  ZTMC VARCHAR2(20) not null,
  YGDM VARCHAR2(10),
  PYDM VARCHAR2(10) not null,
  SFQY NUMBER(1),
  JGID VARCHAR2(20)
);
-- Add comments to the table 
comment on table JC_ZL_ZT01
  is '诊疗计划 | 个人:YGDM值';
-- Add comments to the columns 
comment on column JC_ZL_ZT01.ZTMC
  is '组套名称';
comment on column JC_ZL_ZT01.YGDM
  is '员工代码';
comment on column JC_ZL_ZT01.PYDM
  is '拼音码';
comment on column JC_ZL_ZT01.SFQY
  is '是否启用';
comment on column JC_ZL_ZT01.JGID
  is '机构代码';
-- Create/Recreate primary, unique and foreign key constraints 
alter table JC_ZL_ZT01
  add constraint PK_JC_ZL_ZT01 primary key (ZTBH)
  using index;

-- Create table
create table JC_ZL_ZT02
(
  JLBH NUMBER(18) not null,
  ZTBH NUMBER(18) not null,
  YPZH NUMBER(3) not null,
  XMBH NUMBER(18) not null,
  XMMC VARCHAR2(100) not null,
  XMLX NUMBER(1) not null,
  YCJL NUMBER(10,3),
  SYPC VARCHAR2(12),
  MRCS NUMBER(3),
  YYTS NUMBER(3),
  GYTJ NUMBER(9),
  KSSJ DATE not null,
  JSSJ DATE not null,
  BZXX VARCHAR2(200)
);
-- Add comments to the table 
comment on table JC_ZL_ZT02
  is '门诊_门诊医生组套明细';
-- Add comments to the columns 
comment on column JC_ZL_ZT02.JLBH
  is '记录编号';
comment on column JC_ZL_ZT02.ZTBH
  is '组套编号';
comment on column JC_ZL_ZT02.YPZH
  is '药品组号';
comment on column JC_ZL_ZT02.XMBH
  is '项目编号';
comment on column JC_ZL_ZT02.XMMC
  is '项目名称';
comment on column JC_ZL_ZT02.XMLX
  is '项目类型  | 1：药品 2：项目 3：行为';
comment on column JC_ZL_ZT02.YCJL
  is '一次剂量';
comment on column JC_ZL_ZT02.SYPC
  is '使用频次 | 项目组套维护 使用频次 可以为空';
comment on column JC_ZL_ZT02.MRCS
  is '每日次数';
comment on column JC_ZL_ZT02.YYTS
  is '用药天数';
comment on column JC_ZL_ZT02.GYTJ
  is '给药途径';
comment on column JC_ZL_ZT02.KSSJ
  is '开始时间';
comment on column JC_ZL_ZT02.JSSJ
  is '结束时间';
comment on column JC_ZL_ZT02.BZXX
  is '备注信息';
-- Create/Recreate primary, unique and foreign key constraints 
alter table JC_ZL_ZT02
  add constraint PK_JC_ZL_ZT02 primary key (JLBH)
  using index ;

-- Create table
create table JC_TBKK
(
  JKXH NUMBER(18) not null,
  JGID VARCHAR2(20) default 1 not null,
  ZYH  NUMBER(18) default 0 not null,
  JKRQ DATE not null,
  JKJE NUMBER(10,2) default 0 not null,
  JKFS NUMBER(6) not null,
  SJHM VARCHAR2(20) not null,
  ZPHM VARCHAR2(20),
  JSCS NUMBER(3) default 0 not null,
  CZGH VARCHAR2(10),
  JZRQ DATE,
  HZRQ DATE,
  ZFRQ DATE,
  ZFGH VARCHAR2(10),
  ZFPB NUMBER(1) default 0 not null,
  ZCPB NUMBER(1) default 0 not null
);
-- Add comments to the columns 
comment on column JC_TBKK.JKXH
  is '缴款序号';
comment on column JC_TBKK.JGID
  is '机构代码';
comment on column JC_TBKK.ZYH
  is '家床号';
comment on column JC_TBKK.JKRQ
  is '缴款日期';
comment on column JC_TBKK.JKJE
  is '缴款金额';
comment on column JC_TBKK.JKFS
  is '缴款方式 | 1:现金,2:支票(与ZY_JKFS表中JKFS字段对应';
comment on column JC_TBKK.SJHM
  is '收据号码 | 病人预缴款收据号码';
comment on column JC_TBKK.ZPHM
  is '支票号码 | 病人缴款为支票时的支票号码';
comment on column JC_TBKK.JSCS
  is '"结算次数 | 同ZY_BRRY,ZY_ZYJS,ZY_FYMX表中JSCS
JSCS=0 则病人尚未办理结算."';
comment on column JC_TBKK.CZGH
  is '操作工号';
comment on column JC_TBKK.JZRQ
  is '结账日期';
comment on column JC_TBKK.HZRQ
  is '汇总日期';
comment on column JC_TBKK.ZFRQ
  is '作废日期';
comment on column JC_TBKK.ZFGH
  is '作废工号';
comment on column JC_TBKK.ZFPB
  is '作废判别 | 注销预缴款或发票作废时填写';
comment on column JC_TBKK.ZCPB
  is '转存判别 | 0.正常缴款;    1.中结转存';
-- Create/Recreate primary, unique and foreign key constraints 
alter table JC_TBKK
  add constraint PK_JC_TBKK primary key (JKXH)
  using index;


--2015.3.16 update by gaof 
-- Create table
create table JC_BRRY
(
  ZYH       NUMBER(18) default 0 not null,
  JGID      VARCHAR2(20) default 1 not null,
  BRID      NUMBER(18) not null,
  ZYHM      VARCHAR2(10) not null,
  MZHM      VARCHAR2(32),
  BRXZ      NUMBER(18) not null,
  BRXM      VARCHAR2(40) not null,
  BRXB      NUMBER(4) not null,
  CSNY      DATE,
  SFZH      VARCHAR2(20),
  LXRM      VARCHAR2(20),
  LXGX      NUMBER(4),
  LXDZ      VARCHAR2(100),
  LXDH      VARCHAR2(16),
  DJRQ      DATE not null,
  RYRQ      DATE,
  CYRQ      DATE,
  CYPB      NUMBER(2) default 0,
  CYFS      NUMBER(1) default 0,
  CZGH      VARCHAR2(10),
  RYQK      NUMBER(4),
  BRQK      NUMBER(4),
  HLJB      NUMBER(4),
  YSDM      NUMBER(4),
  ZRYS      VARCHAR2(10),
  ZRHS      VARCHAR2(10),
  QZRQ      DATE,
  KSRQ      DATE,
  JSRQ      DATE,
  JSCS      NUMBER(3) default 0,
  JZRQ      DATE,
  HZRQ      DATE,
  YBKH      VARCHAR2(20),
  JZKH      VARCHAR2(40),
  SPJE      NUMBER(12,2) default 0,
  BZ        VARCHAR2(250),
  CSD_SQS   NUMBER(18),
  CSD_S     NUMBER(18),
  CSD_X     NUMBER(18),
  JGDM_SQS  NUMBER(18),
  JGDM_S    NUMBER(18),
  XZZ_SQS   NUMBER(18),
  XZZ_S     NUMBER(18),
  XZZ_X     NUMBER(18),
  XZZ_YB    VARCHAR2(20),
  XZZ_DH    VARCHAR2(20),
  HKDZ_SQS  NUMBER(18),
  HKDZ_S    NUMBER(18),
  HKDZ_X    NUMBER(18),
  XZZ_QTDZ  VARCHAR2(60),
  HKDZ_QTDZ VARCHAR2(60),
  RYNL      VARCHAR2(20),
  RYZD      VARCHAR2(255),
  MQZD      VARCHAR2(255),
  ZYZD      VARCHAR2(255),
  MZZD      NUMBER(18),
  JCLX      NUMBER(1),
  JCZD      VARCHAR2(255),
  ICD10     VARCHAR2(20),
  JCHM      VARCHAR2(10),
  ZDRQ      DATE,
  BQZY      VARCHAR2(255),
  JCYJ      VARCHAR2(255),
  SQFS      NUMBER(1)
);
-- Add comments to the table 
comment on table JC_BRRY
  is '住院_病人入院';
-- Add comments to the columns 
comment on column JC_BRRY.ZYH
  is '家床号 | 家床内码';
comment on column JC_BRRY.JGID
  is '机构代码';
comment on column JC_BRRY.BRID
  is '病人ID';
comment on column JC_BRRY.ZYHM
  is '家床号码 | 家床病人的识别号，家床病人唯一';
comment on column JC_BRRY.MZHM
  is '门诊号码';
comment on column JC_BRRY.BRXZ
  is '病人性质 | 对应GY_BRXZ中代码';
comment on column JC_BRRY.BRXM
  is '病人姓名';
comment on column JC_BRRY.BRXB
  is '病人性别';
comment on column JC_BRRY.CSNY
  is '出生年月';
comment on column JC_BRRY.SFZH
  is '身份证号';
comment on column JC_BRRY.LXRM
  is '联系人名';
comment on column JC_BRRY.LXGX
  is '联系人关系 | 与GY_DMZD（DMLB=4）对应';
comment on column JC_BRRY.LXDZ
  is '联系地址';
comment on column JC_BRRY.LXDH
  is '联系电话';
comment on column JC_BRRY.DJRQ
  is '登记日期 | 填写入院登记表的时间(系统自动填写)';
comment on column JC_BRRY.RYRQ
  is '入院日期 | 入院登记时填写的入院时间';
comment on column JC_BRRY.CYRQ
  is '出院日期 | 办理出院证明的日期(可以提前或推后)';
comment on column JC_BRRY.CYPB
  is '出院判别 | 0：在院病人
1：撤床证明
2：预结出院
8：正常出院
9：终结出院
99 注销出院';
comment on column JC_BRRY.CYFS
  is '出院方式 |与GY_DMZD（DMLB= 23）对应
1：治愈
2：好转
3：未愈
4：死亡
5：其他
';
comment on column JC_BRRY.CZGH
  is '操作工号';
comment on column JC_BRRY.RYQK
  is '入院情况 | 与GY_DMZD（DMLB=10）对应';
comment on column JC_BRRY.BRQK
  is '病人情况 | 与GY_DMZD（DMLB=10）对应';
comment on column JC_BRRY.HLJB
  is '护理级别 | 0：特级护理
1：一级护理
2：二级护理
3：三级护理
';
comment on column JC_BRRY.YSDM
  is '饮食代码 | 与GY_DMZD（DMLB=20）对应';
comment on column JC_BRRY.ZRYS
  is '责任医生 | 对应GY_YGDM中的YGDM的代码';
comment on column JC_BRRY.ZRHS
  is '责任护士 | 对应GY_YGDM中的YGDM的代码';
comment on column JC_BRRY.QZRQ
  is '确诊日期 | 病人确诊日期 ';
comment on column JC_BRRY.KSRQ
  is '开始日期 | 家床开始时间';
comment on column JC_BRRY.JSRQ
  is '结束日期 | 家床结束日期';
comment on column JC_BRRY.JSCS
  is '结算次数 | 最后一次结算次数（包括作废）
下一次结算次数为当前值加1
';
comment on column JC_BRRY.JZRQ
  is '结账日期 | 汇总日报结束时填写，若该病人已做预结则写该字段';
comment on column JC_BRRY.HZRQ
  is '汇总日期 | 汇总日报结束时填写，病人费用、医嘱转到出院表中，根据该字段可判断病人费用、医嘱在哪张表中';
comment on column JC_BRRY.YBKH
  is '医保卡号';
comment on column JC_BRRY.JZKH
  is '就诊卡号';
comment on column JC_BRRY.SPJE
  is '审批金额 | 允许病人欠款的最大金额';
comment on column JC_BRRY.BZ
  is '备注';
comment on column JC_BRRY.CSD_SQS
  is '出生地_省市区';
comment on column JC_BRRY.CSD_S
  is '出生地_市';
comment on column JC_BRRY.CSD_X
  is '出生地_县';
comment on column JC_BRRY.JGDM_SQS
  is '籍贯代码_省区市';
comment on column JC_BRRY.JGDM_S
  is '籍贯代码_市';
comment on column JC_BRRY.XZZ_SQS
  is '籍贯代码_市';
comment on column JC_BRRY.XZZ_S
  is '现住址_市';
comment on column JC_BRRY.XZZ_X
  is '现住址_县';
comment on column JC_BRRY.XZZ_YB
  is '现住址_邮编';
comment on column JC_BRRY.XZZ_DH
  is '现住址_电话';
comment on column JC_BRRY.HKDZ_SQS
  is '户口地址_省区市';
comment on column JC_BRRY.HKDZ_S
  is '户口地址_市';
comment on column JC_BRRY.HKDZ_X
  is '户口地址_县';
comment on column JC_BRRY.XZZ_QTDZ
  is '现住址_其他地址';
comment on column JC_BRRY.HKDZ_QTDZ
  is '户口地址_其他地址';
comment on column JC_BRRY.RYNL
  is '入院年龄';
comment on column JC_BRRY.RYZD
  is '入院诊断';
comment on column JC_BRRY.MQZD
  is '目前诊断';
comment on column JC_BRRY.ZYZD
  is '主要诊断名称';
comment on column JC_BRRY.MZZD
  is '门诊诊断编码';
comment on column JC_BRRY.JCLX
  is '家床类型';
comment on column JC_BRRY.JCZD
  is '建床诊断';
comment on column JC_BRRY.ICD10
  is 'ICD10';
comment on column JC_BRRY.JCHM
  is '家床号码';
comment on column JC_BRRY.ZDRQ
  is '诊断日期';
comment on column JC_BRRY.BQZY
  is '病情摘要';
comment on column JC_BRRY.JCYJ
  is '收治指征和建床意见';
comment on column JC_BRRY.SQFS
  is '申请方式|1.门诊2.住院3.中心';
-- Create/Recreate primary, unique and foreign key constraints 
alter table JC_BRRY
  add constraint PK_JC_BRRY primary key (ZYH)
  using index ;


--2015.3.13 update by caijy for  家床退药表,费用表
-- Create table
create table JC_FYMX
(
  JLXH NUMBER(18) not null,
  JGID VARCHAR2(20) default '1' not null,
  ZYH  NUMBER(18) not null,
  FYRQ DATE not null,
  FYXH NUMBER(18) not null,
  FYMC VARCHAR2(100),
  YPCD NUMBER(18) default 0 not null,
  FYSL NUMBER(10,2) default 0 not null,
  FYDJ NUMBER(10,4) default 0 not null,
  ZJJE NUMBER(12,2) default 0 not null,
  ZFJE NUMBER(12,2) default 0 not null,
  YSGH VARCHAR2(10),
  SRGH VARCHAR2(10),
  QRGH VARCHAR2(10),
  FYBQ NUMBER(18) ,
  FYKS NUMBER(18),
  ZXKS NUMBER(18),
  JFRQ DATE not null,
  XMLX NUMBER(2) not null,
  YPLX NUMBER(1) default 0 not null,
  FYXM NUMBER(18) not null,
  JSCS NUMBER(3) default 0 not null,
  ZFBL NUMBER(4,3) default 1 not null,
  YZXH NUMBER(18),
  HZRQ DATE,
  YJRQ VARCHAR2(8),
  ZLJE NUMBER(12,2) default 0 not null,
  ZLXZ NUMBER(4),
  YEPB NUMBER(1) default 0,
  DZBL NUMBER(6,3) default 0 not null
);
-- Add comments to the table 
comment on table JC_FYMX
  is '家床费用明细表';
-- Add comments to the columns 
comment on column JC_FYMX.JLXH
  is '记录序号';
comment on column JC_FYMX.JGID
  is '机构ID';
comment on column JC_FYMX.ZYH
  is '住院号';
comment on column JC_FYMX.FYRQ
  is '费用日期';
comment on column JC_FYMX.FYXH
  is '费用序号';
comment on column JC_FYMX.FYMC
  is '费用名称';
comment on column JC_FYMX.YPCD
  is '药品产地';
comment on column JC_FYMX.FYSL
  is '费用数量';
comment on column JC_FYMX.FYDJ
  is '费用单价';
comment on column JC_FYMX.ZJJE
  is '总计金额';
comment on column JC_FYMX.ZFJE
  is '自负金额';
comment on column JC_FYMX.YSGH
  is '医生工号';
comment on column JC_FYMX.SRGH
  is '输入工号';
comment on column JC_FYMX.QRGH
  is '确认工号';
comment on column JC_FYMX.FYBQ
  is '费用病区 | 费用发生的病区';
comment on column JC_FYMX.FYKS
  is '费用科室 | 费用输入的科室(记帐,按输入科室核算时要用)';
comment on column JC_FYMX.ZXKS
  is '执行科室 | 费用记帐科室(记帐,按执行科室核算时使用)';
comment on column JC_FYMX.JFRQ
  is '记费日期 | 实际记费日期 写JC_FYMX时服务器时间';
comment on column JC_FYMX.XMLX
  is '项目类型 | 1：病区系统记帐 2：药房系统记帐 3：医技系统记帐 4：住院系统记帐 5：手术麻醉记帐 9：自动累加费用';
comment on column JC_FYMX.YPLX
  is '药品类型 | 0：费用 1：西药 2：中成药 3：中草药';
comment on column JC_FYMX.FYXM
  is '费用项目 | 指定FYXH归并的项目(同GY_SFMX表中SFXM对应)';
comment on column JC_FYMX.JSCS
  is '结算次数';
comment on column JC_FYMX.ZFBL
  is '自负比例 | 同GY_ZFBL或 GY_YPJY,GY_FYJY表中自负比例对应';
comment on column JC_FYMX.YZXH
  is '医嘱序号 | 同ZY_BQYZ表中的医嘱序号对应';
comment on column JC_FYMX.HZRQ
  is '汇总日期';
comment on column JC_FYMX.YJRQ
  is '月结日期';
comment on column JC_FYMX.ZLJE
  is '自理金额';
comment on column JC_FYMX.ZLXZ
  is '诊疗小组';
comment on column JC_FYMX.YEPB
  is '婴儿判别 | 0,大人  1  小孩';
comment on column JC_FYMX.DZBL
  is '打折比例';
-- Create/Recreate primary, unique and foreign key constraints 
alter table JC_FYMX
  add constraint PK_JC_FYMX primary key (JLXH)
  using index ;
-- Create/Recreate check constraints 
alter table JC_FYMX
  add constraint CKC_YPLX_JC_FYMX
  check (YPLX IN (0,1,2,3));
-- Create/Recreate indexes 
create index IDX_JC_FYMX_FYRQ on JC_FYMX (FYRQ);
create index IDX_JC_FYMX_JZRQ on JC_FYMX (HZRQ);
create index IDX_JC_FYMX_ZYH on JC_FYMX (ZYH);

-- Create table
create table JC_TYMX
(
  JLXH NUMBER(18) not null,
  ZYH  NUMBER(18) not null,
  YPXH NUMBER(18) not null,
  YPCD NUMBER(18) not null,
  SQRQ DATE not null,
  YPJG NUMBER(12,6) default 0 not null,
  JGID VARCHAR2(20) not null,
  TYRQ DATE,
  YPGG VARCHAR2(20),
  YFDW VARCHAR2(4),
  YFBZ NUMBER(4),
  YPSL NUMBER(8,4) default 0,
  ZFBL NUMBER(4,3) default 1,
  YEPB NUMBER(1) default 0,
  YFSB NUMBER(18),
  TYBQ NUMBER(18),
  CZGH VARCHAR2(10),
  ZFPB NUMBER(2) default 0,
  TJBZ NUMBER(1) default 0,
  YZID NUMBER(18),
  TYLX NUMBER(1),
  THBZ NUMBER(8) not null,
  THSJ DATE,
  THR  VARCHAR2(10),
  JLID NUMBER(18)
);
-- Add comments to the table 
comment on table JC_TYMX
  is '病区_退药明细';
-- Add comments to the columns 
comment on column JC_TYMX.JLXH
  is '记录序号';
comment on column JC_TYMX.ZYH
  is '住院号';
comment on column JC_TYMX.YPXH
  is '药品序号';
comment on column JC_TYMX.YPCD
  is '药品产地';
comment on column JC_TYMX.SQRQ
  is '申请日期';
comment on column JC_TYMX.YPJG
  is '药品价格';
comment on column JC_TYMX.JGID
  is '机构代码';
comment on column JC_TYMX.TYRQ
  is '退费日期';
comment on column JC_TYMX.YPGG
  is '药品规格';
comment on column JC_TYMX.YFDW
  is '药房单位';
comment on column JC_TYMX.YFBZ
  is '药房包装';
comment on column JC_TYMX.YPSL
  is '药品数量';
comment on column JC_TYMX.ZFBL
  is '自负比例';
comment on column JC_TYMX.YEPB
  is '婴儿判别';
comment on column JC_TYMX.YFSB
  is '药房识别';
comment on column JC_TYMX.TYBQ
  is '退药病区';
comment on column JC_TYMX.CZGH
  is '操作工号';
comment on column JC_TYMX.ZFPB
  is '自费判别';
comment on column JC_TYMX.TJBZ
  is '提交标志';
comment on column JC_TYMX.YZID
  is '医嘱ID';
comment on column JC_TYMX.TYLX
  is '退药类型';
comment on column JC_TYMX.THBZ
  is '退回标志';
comment on column JC_TYMX.THSJ
  is '退回时间';
comment on column JC_TYMX.THR
  is '退回人';
comment on column JC_TYMX.JLID
  is '记录ID';
-- Create/Recreate primary, unique and foreign key constraints 
alter table JC_TYMX
  add constraint PK_JC_TYMX primary key (JLXH)
  using index ;

----2015-3-12
alter table JC_BRRY add SQFS NUMBER(1);
comment on column jc_brry.SQFS
  is '申请方式|1.门诊2.住院3.中心';

--2015-3-11 加了几个字段(为检查检验系统)
CREATE OR REPLACE VIEW GY_YGDM AS
SELECT PERSONID          YGDM,
       PERSONNAME        YGXM,
       PHOTO             YGZP,
       CARDNUM           SFZH,
       BIRTHDAY          CSNY,
       GENDER            YGXB,
       EMAIL             YXDZ,
       MOBILE            SJHM,
       OFFICECODE        KSDM,
       ORGANIZCODE       JGID,
       PYCODE            PYDM,
       WBCODE            WBDM,
       JXCODE            JXDM,
       QTCODE            QTDM,
       ISEXPERT          ZJPB,
       EXPERTCOST        ZJFY,
       PRESCRIBERIGHT    KCFQ,
       NARCOTICRIGHT     MZYQ,
       PSYCHOTROPICRIGHT JSYQ,
       ANTIBIOTICRIGHT   KSYQ,
       LOGOFF            ZFPB,
       PERSONID          YGBH
  FROM SYS_PERSONNEL
--2015.3.10 create by yaosq for 病历模版的健康处方
alter table PUB_PelpleHealthTeach_MB add diagnoseId VARCHAR2(16);

--2015.3.4 update by gaof for  家床申请
-- Create table
create table JC_BRSQ
(
  ID    INTEGER not null,
  SQRQ  DATE,
  SQYS  VARCHAR2(50),
  BQZY  VARCHAR2(500),
  JCYJ  VARCHAR2(500),
  SQZT  VARCHAR2(50),
  BRID  NUMBER(18),
  LXR   VARCHAR2(50),
  YHGX  VARCHAR2(50),
  LXDH  VARCHAR2(50),
  ZDRQ  DATE,
  JGID  VARCHAR2(20) not null,
  JCZD  VARCHAR2(255),
  ICD10 VARCHAR2(20),
  MZHM  VARCHAR2(20),
  ZYHM  VARCHAR2(20),
  LXDZ  VARCHAR2(100),
  BRXZ  VARCHAR2(10),
  SQFS  NUMBER(1),
  BRXM  VARCHAR2(50),
  BRXB  NUMBER(1),
  CSNY  DATE,
  SFZH  VARCHAR2(20),
  BRNL  VARCHAR2(10)
);
-- Add comments to the table 
comment on table JC_BRSQ
  is '家床申请';
-- Add comments to the columns 
comment on column JC_BRSQ.ID
  is '家床申请ID';
comment on column JC_BRSQ.SQRQ
  is '申请日期';
comment on column JC_BRSQ.SQYS
  is '申请医生';
comment on column JC_BRSQ.BQZY
  is '病情摘要';
comment on column JC_BRSQ.JCYJ
  is '收治指征和建床意见';
comment on column JC_BRSQ.SQZT
  is '申请状态  1=未提交 2=已提交 3=退回 4=已登记 5=家床中 6=撤床后';
comment on column JC_BRSQ.BRID
  is '病人ID';
comment on column JC_BRSQ.LXR
  is '联系人';
comment on column JC_BRSQ.YHGX
  is '与患关系';
comment on column JC_BRSQ.LXDH
  is '联系电话';
comment on column JC_BRSQ.ZDRQ
  is '诊断日期';
comment on column JC_BRSQ.JGID
  is '机构代码 机构ID';
comment on column JC_BRSQ.JCZD
  is '建床诊断（调入门诊主诊断或住院出院住诊断）';
comment on column JC_BRSQ.ICD10
  is 'ICD10';
comment on column JC_BRSQ.MZHM
  is '门诊号码';
comment on column JC_BRSQ.ZYHM
  is '住院号码';
comment on column JC_BRSQ.LXDZ
  is '联系地址';
comment on column JC_BRSQ.BRXZ
  is '病人性质';
comment on column JC_BRSQ.SQFS
  is '申请方式  1=门诊号码 2=住院号码';
comment on column JC_BRSQ.BRXM
  is '病人姓名';
comment on column JC_BRSQ.BRXB
  is '病人性别';
comment on column JC_BRSQ.CSNY
  is '出生年月';
comment on column JC_BRSQ.SFZH
  is '身份证号';
comment on column JC_BRSQ.BRNL
  is '病人年龄 存储用到,查询没用到';
-- Create/Recreate primary, unique and foreign key constraints 
alter table JC_BRSQ
  add constraint PK_JC_BRSQ primary key (ID)
  using index ;


--2015.2.4 update by caijy for  家床发药表
-- Create table
create table JC_FYJL
(
  JGID VARCHAR2(20) default '1' not null,
  JLID NUMBER(18) not null,
  FYSJ DATE not null,
  FYGH VARCHAR2(10),
  FYBQ NUMBER(18),
  FYLX NUMBER(1) default 1 not null,
  YFSB NUMBER(18),
  FYFS NUMBER(18) default 0 not null,
  DYPB NUMBER(1) default 0 not null
);
-- Add comments to the table 
comment on table JC_FYJL
  is '家床_发药记录表';
-- Add comments to the columns 
comment on column JC_FYJL.JGID
  is '机构ID';
comment on column JC_FYJL.JLID
  is '记录ID';
comment on column JC_FYJL.FYSJ
  is '发药时间';
comment on column JC_FYJL.FYGH
  is '发药工号';
comment on column JC_FYJL.FYBQ
  is '发药病区';
comment on column JC_FYJL.FYLX
  is '发药类型 | 1：普通发药 2：急诊用药 3：出院带药 4：医技用药 5：三级库房发药';
comment on column JC_FYJL.YFSB
  is '药房识别 | 对应YF_YFLB的代码';
comment on column JC_FYJL.FYFS
  is '发药方式 | 对应ZY_FYFS中的代码';
comment on column JC_FYJL.DYPB
  is '打印判断 | 0：未打印      1：已打印';
-- Create/Recreate primary, unique and foreign key constraints 
alter table JC_FYJL
  add constraint PK_JC_FYJL primary key (JLID)
  using index ;
-- Create table
create table YF_JCFYMX
(
  JGID   VARCHAR2(20) not null,
  JLXH   NUMBER(18) not null,
  YFSB   NUMBER(18) not null,
  CKBH   NUMBER(2) not null,
  FYLX   NUMBER(1),
  ZYH    NUMBER(18) not null,
  FYRQ   DATE not null,
  YPXH   NUMBER(18) default 0 not null,
  YPCD   NUMBER(18) default 0 not null,
  YPGG   VARCHAR2(20),
  YFDW   VARCHAR2(4),
  YFBZ   NUMBER(4) default 1 not null,
  YPSL   NUMBER(10,4) default 0 not null,
  YPDJ   NUMBER(12,6) default 0 not null,
  ZFBL   NUMBER(4,3) default 1,
  QRGH   VARCHAR2(10),
  JFRQ   DATE not null,
  YPLX   NUMBER(1) not null,
  FYKS   NUMBER(18) default 0 not null,
  LYBQ   NUMBER(18) ,
  ZXKS   NUMBER(18) default 0 not null,
  YZXH   NUMBER(18),
  YEPB   NUMBER(1),
  ZFPB   NUMBER(2) default 0,
  FYFS   NUMBER(18) default 1 not null,
  JLID   NUMBER(18) default 0 not null,
  LSJG   NUMBER(12,6) default 0 not null,
  PFJG   NUMBER(12,6) default 0 not null,
  JHJG   NUMBER(12,6) default 0 not null,
  FYJE   NUMBER(12,4) default 0 not null,
  LSJE   NUMBER(12,4) default 0 not null,
  PFJE   NUMBER(12,4) default 0 not null,
  JHJE   NUMBER(12,4) default 0 not null,
  YPPH   VARCHAR2(20),
  YPXQ   DATE,
  TYGL   NUMBER(18) not null,
  JFID   NUMBER(18) default 0 not null,
  YKJH   NUMBER(12,4) default 0,
  JBYWBZ NUMBER(1) default 0 not null,
  KCSB   NUMBER(18) default 0 not null,
  TJXH   NUMBER(18),
  TYXH   NUMBER(18)
);
-- Add comments to the table 
comment on table YF_JCFYMX
  is '住院病人发药明细';
-- Add comments to the columns 
comment on column YF_JCFYMX.JGID
  is '机构ID';
comment on column YF_JCFYMX.JLXH
  is '记录序号';
comment on column YF_JCFYMX.YFSB
  is '药房识别';
comment on column YF_JCFYMX.CKBH
  is '窗口编号';
comment on column YF_JCFYMX.FYLX
  is '发药类型';
comment on column YF_JCFYMX.ZYH
  is '住院号';
comment on column YF_JCFYMX.FYRQ
  is '费用日期';
comment on column YF_JCFYMX.YPXH
  is '费用序号';
comment on column YF_JCFYMX.YPCD
  is '药品产地';
comment on column YF_JCFYMX.YPGG
  is '药品规格';
comment on column YF_JCFYMX.YFDW
  is '药房单位';
comment on column YF_JCFYMX.YFBZ
  is '药房包装';
comment on column YF_JCFYMX.YPSL
  is '费用数量';
comment on column YF_JCFYMX.YPDJ
  is '费用单价';
comment on column YF_JCFYMX.ZFBL
  is '自负比例';
comment on column YF_JCFYMX.QRGH
  is '确认工号';
comment on column YF_JCFYMX.JFRQ
  is '记费日期';
comment on column YF_JCFYMX.YPLX
  is '药品类型';
comment on column YF_JCFYMX.FYKS
  is '费用科室';
comment on column YF_JCFYMX.LYBQ
  is '领药病区';
comment on column YF_JCFYMX.ZXKS
  is '执行科室';
comment on column YF_JCFYMX.YZXH
  is '医嘱序号';
comment on column YF_JCFYMX.YEPB
  is '婴儿判别';
comment on column YF_JCFYMX.ZFPB
  is '自负判别';
comment on column YF_JCFYMX.FYFS
  is '发药方式';
comment on column YF_JCFYMX.JLID
  is '记录ID';
comment on column YF_JCFYMX.LSJG
  is '零售价格';
comment on column YF_JCFYMX.PFJG
  is '批发价格';
comment on column YF_JCFYMX.JHJG
  is '进货价格';
comment on column YF_JCFYMX.FYJE
  is '费用金额';
comment on column YF_JCFYMX.LSJE
  is '零售金额';
comment on column YF_JCFYMX.PFJE
  is '批发金额';
comment on column YF_JCFYMX.JHJE
  is '进货金额';
comment on column YF_JCFYMX.YPPH
  is '药品批号';
comment on column YF_JCFYMX.YPXQ
  is '药品效期';
comment on column YF_JCFYMX.TYGL
  is '退药关联 | 普通的记录，TYGL等于本条记录的JLXH 对于退药的记录,TYGL等于退的那一条记录的JLXH';
comment on column YF_JCFYMX.JFID
  is '记费ID | 与ZY_FYMX中的 JLXH 对应';
comment on column YF_JCFYMX.YKJH
  is '药库进货';
comment on column YF_JCFYMX.JBYWBZ
  is '基本药物标志';
comment on column YF_JCFYMX.KCSB
  is '库存识别';
comment on column YF_JCFYMX.TJXH
  is '提交序号 | 对应BQ_TJ02的主键序号值';
comment on column YF_JCFYMX.TYXH
  is '退药序号 | 对应JC_TYMX表的主键值';
-- Create/Recreate primary, unique and foreign key constraints 
alter table YF_JCFYMX
  add constraint PK_YF_JCFYMX primary key (JLXH)
  using index ;
-- Create/Recreate indexes 
create index IDX_YF_JCFYMX_FYRQ on YF_JCFYMX (FYRQ);
create index IDX_YF_JCFYMX_JFRQ on YF_JCFYMX (JFRQ);
create index IDX_YF_JCFYMX_JLID on YF_JCFYMX (JLID);
create index IDX_YF_JCFYMX_YFSB on YF_JCFYMX (YFSB);
create index IDX_YF_JCFYMX_YZXH on YF_JCFYMX (YZXH);
create index IDX_YF_JCFYMX_ZYH on YF_JCFYMX (ZYH);

--2015.2.2 update by caijy for  家床药品提交表
-- Create table
create table JC_TJ02
(
  JLXH   NUMBER(18) not null,
  JGID   VARCHAR2(20) default '1' not null,
  TJXH   NUMBER(18) not null,
  YZXH   NUMBER(18) not null,
  ZYH    NUMBER(18) not null,
  YPXH   NUMBER(18) not null,
  YPCD   NUMBER(18) not null,
  YFGG   VARCHAR2(20),
  YFDW   VARCHAR2(4),
  YFBZ   NUMBER(4) not null,
  KSSJ   DATE not null,
  YCSL   NUMBER(8,2) default 0 not null,
  YTCS   NUMBER(2) default 0 not null,
  FYSL   NUMBER(8,2) default 0 not null,
  JFRQ   DATE not null,
  QRRQ   DATE not null,
  SYPC   VARCHAR2(6),
  FYJE   NUMBER(12,2) default 0 not null,
  YPDJ   NUMBER(12,4) default 0 not null,
  FYBZ   NUMBER(1) default 0 not null,
  FYGH   VARCHAR2(10),
  FYRQ   DATE,
  LSYZ   NUMBER(1),
  QZCL   NUMBER default 0 not null,
  YEPB   NUMBER(1) default 0,
  FYKS   NUMBER(18),
  SJFYBZ NUMBER(1) not null,
  SJFYR  VARCHAR2(10),
  SJFYSJ DATE,
  YYTS   NUMBER(4)
);
-- Add comments to the table 
comment on table JC_TJ02
  is '家床_提交明细 | 记录病区医嘱提交明细的记录，与提交记录表通过TJXH关联';
-- Add comments to the columns 
comment on column JC_TJ02.JLXH
  is '记录序号';
comment on column JC_TJ02.JGID
  is '机构代码';
comment on column JC_TJ02.TJXH
  is '提交序号';
comment on column JC_TJ02.YZXH
  is '医嘱序号';
comment on column JC_TJ02.ZYH
  is '住院号';
comment on column JC_TJ02.YPXH
  is '药品序号';
comment on column JC_TJ02.YPCD
  is '药品产地';
comment on column JC_TJ02.YFGG
  is '药房规格';
comment on column JC_TJ02.YFDW
  is '药房单位';
comment on column JC_TJ02.YFBZ
  is '药房包装';
comment on column JC_TJ02.KSSJ
  is '开始时间';
comment on column JC_TJ02.YCSL
  is '一次数量';
comment on column JC_TJ02.YTCS
  is '一天次数';
comment on column JC_TJ02.FYSL
  is '发药时间';
comment on column JC_TJ02.JFRQ
  is '计费日期';
comment on column JC_TJ02.QRRQ
  is '确认日期';
comment on column JC_TJ02.SYPC
  is '使用频次';
comment on column JC_TJ02.FYJE
  is '发药金额';
comment on column JC_TJ02.YPDJ
  is '药品单价';
comment on column JC_TJ02.FYBZ
  is '发药标志';
comment on column JC_TJ02.FYGH
  is '发药工号';
comment on column JC_TJ02.FYRQ
  is '发药日期';
comment on column JC_TJ02.LSYZ
  is '临时医嘱';
comment on column JC_TJ02.QZCL
  is '取整策略';
comment on column JC_TJ02.YEPB
  is '婴儿判别';
comment on column JC_TJ02.FYKS
  is '费用科室';
comment on column JC_TJ02.SJFYBZ
  is '实际发药标志';
comment on column JC_TJ02.SJFYR
  is '实际发药人';
comment on column JC_TJ02.SJFYSJ
  is '实际发药时间';
comment on column JC_TJ02.YYTS
  is '用药天数';
-- Create/Recreate primary, unique and foreign key constraints 
alter table JC_TJ02
  add constraint PK_JC_TJ02 primary key (JLXH)
  using index ;
-- Create/Recreate indexes 
create index IDX_JC_TJ02_TJXH on JC_TJ02 (TJXH);
create index IDX_JC_TJ02_YZXH on JC_TJ02 (YZXH);
-- Create table
create table JC_TJ01
(
  TJXH NUMBER(18) not null,
  JGID VARCHAR2(20) default '1' not null,
  TJYF NUMBER(18) not null,
  YZLX NUMBER(2) not null,
  FYFS NUMBER(18),
  XMLX NUMBER(2) not null,
  TJSJ DATE,
  TJBQ NUMBER(8) not null,
  TJGH VARCHAR2(10) not null,
  FYBZ NUMBER(1) default 0 not null
);
-- Add comments to the table 
comment on table JC_TJ01
  is '家床_提交记录 | 记录病区医嘱提交的记录，与提交明细表JC_TJMX通过TJXH关联';
-- Add comments to the columns 
comment on column JC_TJ01.TJXH
  is '提交序号';
comment on column JC_TJ01.JGID
  is '机构代码';
comment on column JC_TJ01.TJYF
  is '提交药房';
comment on column JC_TJ01.YZLX
  is '医嘱类型';
comment on column JC_TJ01.FYFS
  is '发药方式';
comment on column JC_TJ01.XMLX
  is '项目类型';
comment on column JC_TJ01.TJSJ
  is '提交时间';
comment on column JC_TJ01.TJBQ
  is '提交病区';
comment on column JC_TJ01.TJGH
  is '提交工号';
comment on column JC_TJ01.FYBZ
  is '发药标志';
-- Create/Recreate primary, unique and foreign key constraints 
alter table JC_TJ01
  add constraint PK_JC_TJ01 primary key (TJXH)
  using index ;
--2015.1.26 update by caijy for  家床医嘱表增加提交执行字段
alter table JC_BRYZ add TJZX number(1) default 0;
comment on column JC_BRYZ.TJZX
  is '提交执行,1是提交 ,2是执行(或发药)';
--2015.01.23 update by shiwy
-- Create table JC_BG01(医技报告表)
create table JC_BG01
(
  YJXH NUMBER(18) not null,
  MZZY NUMBER(1) not null,
  JGID VARCHAR2(20) not null,
  MBXH NUMBER(18),
  TJHM VARCHAR2(10),
  BRHM VARCHAR2(32),
  ZYH  NUMBER(18),
  BRXM VARCHAR2(40),
  BRXB VARCHAR2(2),
  BRNL NUMBER(3),
  SJYS VARCHAR2(10),
  SJKS NUMBER(18),
  JCYS VARCHAR2(10),
  JCKS NUMBER(18),
  JCRQ TIMESTAMP(6),
  XMXH NUMBER(18),
  XMMC VARCHAR2(80),
  HJJE NUMBER(10,2),
  ZDDM VARCHAR2(10),
  YQDH VARCHAR2(10),
  BBBM VARCHAR2(4),
  YJPH VARCHAR2(20)
);
-- Add comments to the table 
comment on table JC_BG01
  is '医技报告01';
-- Add comments to the columns 
comment on column JC_BG01.YJXH
  is '医技序号';
comment on column JC_BG01.MZZY
  is '门诊住院';
comment on column JC_BG01.JGID
  is '机构ID';
comment on column JC_BG01.MBXH
  is '模板序号';
comment on column JC_BG01.TJHM
  is '特检号码';
comment on column JC_BG01.BRHM
  is '病人号码';
comment on column JC_BG01.ZYH
  is '住院号';
comment on column JC_BG01.BRXM
  is '病人姓名';
comment on column JC_BG01.BRXB
  is '病人性别';
comment on column JC_BG01.BRNL
  is '病人年龄';
comment on column JC_BG01.SJYS
  is '申检医生';
comment on column JC_BG01.SJKS
  is '申检科室';
comment on column JC_BG01.JCYS
  is '检查医生';
comment on column JC_BG01.JCKS
  is '检查科室';
comment on column JC_BG01.JCRQ
  is '检查日期';
comment on column JC_BG01.XMXH
  is '项目序号';
comment on column JC_BG01.XMMC
  is '项目名称';
comment on column JC_BG01.HJJE
  is '合计金额';
comment on column JC_BG01.ZDDM
  is '诊断代码';
comment on column JC_BG01.YQDH
  is '仪器代号';
comment on column JC_BG01.BBBM
  is '标本编码';
comment on column JC_BG01.YJPH
  is '医技片号';

-- Create table JC_YJ01(家床医技01表)
create table JC_YJ01
(
  YJXH NUMBER(18) not null,
  JGID VARCHAR2(20) default 1 not null,
  TJHM VARCHAR2(10),
  ZYH  NUMBER(18),
  ZYHM VARCHAR2(10),
  BRXM VARCHAR2(40),
  KDRQ DATE,
  KSDM NUMBER(18),
  YSDM VARCHAR2(10),
  ZXRQ DATE,
  ZXKS NUMBER(18),
  ZXYS VARCHAR2(10),
  ZXPB NUMBER(1) default 0 not null,
  HJGH VARCHAR2(10),
  BBBM VARCHAR2(4),
  ZYSX VARCHAR2(250),
  ZFPB NUMBER(1) default 0 not null,
  HYMX VARCHAR2(250),
  YJPH VARCHAR2(20),
  SQDH NUMBER(18),
  BWID NUMBER(9),
  JBID NUMBER(18),
  DJZT NUMBER(1) default 0 not null,
  SQWH NUMBER(18),
  FYBQ NUMBER(18),
  TJSJ DATE
);
-- Add comments to the columns 
comment on column JC_YJ01.YJXH 
is '和YJ_ZY02.YJXH主外键关系';
  comment on column JC_YJ01.JGID
  is '机构ID';
  comment on column JC_YJ01.TJHM
  is '特检号码';
  comment on column JC_YJ01.ZYH
  is '住院号';
  comment on column JC_YJ01.ZYHM
  is '住院号码';
  comment on column JC_YJ01.BRXM
  is '病人姓名';
  comment on column JC_YJ01.KDRQ
  is '开单日期';
  comment on column JC_YJ01.KSDM
  is '科室代码';
  comment on column JC_YJ01.YSDM
  is '医生代码';
  comment on column JC_YJ01.ZXRQ
  is '执行日期';
  comment on column JC_YJ01.ZXKS
  is '执行科室';
  comment on column JC_YJ01.ZXYS
  is '执行医生';
comment on column JC_YJ01.ZXPB
  is '医技单执行判别,与同表的ZXKS、ZXYS、ZXRQ同时被赋值
0：未执行     1：已执行';
comment on column JC_YJ01.HJGH
  is '划价工号';
comment on column JC_YJ01.BBBM
  is '与YJ_BBBM.BBBM 列相关联';
  comment on column JC_YJ01.ZYSX
  is '注意事项';
  comment on column JC_YJ01.ZFPB
  is '作废判别';
  comment on column JC_YJ01.HYMX
  is '化验明细';
  comment on column JC_YJ01.YJPH
  is '医技片号';
  comment on column JC_YJ01.SQDH
  is '申请单号';
  comment on column JC_YJ01.BWID
  is '部位编号';
  comment on column JC_YJ01.JBID
  is '疾病编号';
  comment on column JC_YJ01.DJZT
  is '单据状态';
  comment on column JC_YJ01.SQWH
  is '申请文号';
  comment on column JC_YJ01.FYBQ
  is '费用病区';
comment on column JC_YJ01.TJSJ
  is '提交时间';
-- Create table JC_YJ02(家床医技02表)
create table JC_YJ02
(
  SBXH NUMBER(18) not null,
  JGID VARCHAR2(20) default 1 not null,
  YJXH NUMBER(18) not null,
  YLXH NUMBER(6) not null,
  XMLX NUMBER(2),
  YJZX NUMBER(1),
  YLDJ NUMBER(10,2) default 0 not null,
  YLSL NUMBER(8,2) default 0 not null,
  FYGB NUMBER(4),
  ZFBL NUMBER(5,3) default 0 not null,
  YZXH NUMBER(18),
  TPLJ VARCHAR2(50),
  YEPB NUMBER(1) default 0
);
-- Add comments to the table 
comment on table JC_YJ02
  is '住院医技02表';
-- Add comments to the columns 
comment on column JC_YJ02.SBXH
  is '识别序号';
comment on column JC_YJ02.JGID
  is '机构ID';
comment on column JC_YJ02.YJXH
  is '项目的费用序号，和GY_YLSF.FYXH相关联';
comment on column JC_YJ02.YLXH
  is '医技序号';
comment on column JC_YJ02.XMLX
  is '项目类型';
comment on column JC_YJ02.YJZX
  is '0：副项         1：主项';
comment on column JC_YJ02.YLDJ
  is '医疗单价';
comment on column JC_YJ02.YLSL
  is '医疗数量';
comment on column JC_YJ02.FYGB
  is '费用归并';
comment on column JC_YJ02.ZFBL
  is '自负比例';
comment on column JC_YJ02.YZXH
  is '医嘱序号';
comment on column JC_YJ02.TPLJ
  is '医技图片';
comment on column JC_YJ02.YEPB
  is '婴儿唯一号';
-- Create table JC_YGPJ(家床员工票据表)
create table JC_YGPJ
(
  JLXH NUMBER(18) not null,
  JGID VARCHAR2(20) not null,
  YGDM VARCHAR2(10) not null,
  LYRQ DATE not null,
  PJLX NUMBER(1) not null,
  QSHM VARCHAR2(20) not null,
  ZZHM VARCHAR2(20) not null,
  DQHM VARCHAR2(20) not null,
  SYBZ NUMBER(1) default 0 not null
);
-- Add comments to the table 
comment on table JC_YGPJ
  is '住院_员工票据';
-- Add comments to the columns 
comment on column JC_YGPJ.JLXH
  is '记录序号';
comment on column JC_YGPJ.JGID
  is '机构编号';
comment on column JC_YGPJ.YGDM
  is '员工代码';
comment on column JC_YGPJ.LYRQ
  is '领取日期';
comment on column JC_YGPJ.PJLX
  is '票据类型 | 1.发票        2.收据';
comment on column JC_YGPJ.QSHM
  is '起始号码';
comment on column JC_YGPJ.ZZHM
  is '终止号码';
comment on column JC_YGPJ.DQHM
  is '当前号码';
comment on column JC_YGPJ.SYBZ
  is '使用标志 | 0.可用        1.用完';
-- Create table JC_FYYF(家床发药药房表)
create table JC_FYYF
(
  JLXH NUMBER(18) not null,
  JGID VARCHAR2(20) not null,
  GNFL NUMBER(1) default 0,
  TYPE NUMBER(1),
  YFSB NUMBER(18),
  DMSB VARCHAR2(255),
  ZXPB NUMBER(1) default 0
);
-- Add comments to the table 
comment on table JC_FYYF
  is '病区_发药药房';
-- Add comments to the columns 
comment on column JC_FYYF.JLXH
  is '记录序号';
comment on column JC_FYYF.JGID
  is '机构代码';
comment on column JC_FYYF.GNFL
  is '功能分类';
comment on column JC_FYYF.TYPE
  is '医嘱类型';
comment on column JC_FYYF.YFSB
  is '药房识别';
comment on column JC_FYYF.DMSB
  is '代码识别';
comment on column JC_FYYF.ZXPB
  is '注销判别';
--2014.12.23 update by caijy for 药库采购计划建表
-- Create table
create table YK_JH02
(
  JGID VARCHAR2(20) not null,
  SBXH NUMBER(18) not null,
  JHDH NUMBER(6) not null,
  XTSB NUMBER(18) not null,
  YPXH NUMBER(18) not null,
  YPCD NUMBER(18) not null,
  JHSL NUMBER(10,4) default 0 not null,
  GJJG NUMBER(12,6) default 0 not null,
  JHQD NUMBER(6),
  SPSL NUMBER(10,4) default 0 not null,
  CGSL NUMBER(10,4),
  GJJE NUMBER(10,4) default 0 not null
);
-- Add comments to the table 
comment on table YK_JH02
  is '采购计划单明细表';
-- Add comments to the columns 
comment on column YK_JH02.JGID
  is '机构ID';
comment on column YK_JH02.SBXH
  is '识别序号';
comment on column YK_JH02.JHDH
  is '计划单号';
comment on column YK_JH02.XTSB
  is '药库识别';
comment on column YK_JH02.YPXH
  is '药品序号';
comment on column YK_JH02.YPCD
  is '药品产地';
comment on column YK_JH02.JHSL
  is '计划数量';
comment on column YK_JH02.GJJG
  is '估计价格';
comment on column YK_JH02.JHQD
  is '进货渠道';
comment on column YK_JH02.SPSL
  is '审批数量';
comment on column YK_JH02.CGSL
  is '采购数量';
  comment on column YK_JH02.GJJE
  is '参考金额';
-- Create/Recreate primary, unique and foreign key constraints 
alter table YK_JH02
  add constraint PK_YK_JH02 primary key (SBXH)
  using index ;

-- Create table
create table YK_JH01
(
  JGID VARCHAR2(20) not null,
  JHDH NUMBER(6) not null,
  XTSB NUMBER(18) not null,
  JHBZ VARCHAR2(40),
  ZXRQ DATE,
  SPGH VARCHAR2(10),
  BZRQ DATE not null,
  BZGH VARCHAR2(10) not null,
  SPRQ DATE,
  ZXGH VARCHAR2(10),
  DWXH NUMBER(18) not null,
  SBXH NUMBER(18) not null
);
-- Add comments to the table 
comment on table YK_JH01
  is '药库采购计划表';
-- Add comments to the columns 
comment on column YK_JH01.JGID
  is '机构ID';
comment on column YK_JH01.JHDH
  is '计划单号';
comment on column YK_JH01.XTSB
  is '药库识别';
comment on column YK_JH01.JHBZ
  is '备注';
comment on column YK_JH01.ZXRQ
  is '执行日期';
comment on column YK_JH01.SPGH
  is '审批工号';
comment on column YK_JH01.BZRQ
  is '编制日期';
comment on column YK_JH01.BZGH
  is '编制工号';
comment on column YK_JH01.SPRQ
  is '审批日期';
comment on column YK_JH01.ZXGH
  is '执行工号';
comment on column YK_JH01.DWXH
  is '单位序号';
comment on column YK_JH01.SBXH
  is '主键';
-- Create/Recreate primary, unique and foreign key constraints 
alter table YK_JH01
  add constraint PK_YK_JH01 primary key (SBXH)
  using index ;
alter table YK_JH01
  add constraint UK_YK_JH01 unique (JHDH, XTSB)
  using index ;
  
  
--2014.1.20 update by zhoubn for YK_TYPK
alter table YK_TYPK add YYBS number(1);
comment on column YK_TYPK.YYBS  is '用药标识  | 1.高血压、2.糖尿病、3.精神病、4.胰岛素';
alter table YK_TYPK add GMYWLB number(1);
comment on column YK_TYPK.GMYWLB  is '过敏药物类别  | 1.青霉素、2.磺胺、3.链霉素';  
  
  
--2015.1.16 create by zhangyq for phis2.4.2
create table MS_GHYJ
(
  YJXH NUMBER(18) not null,
  JGID VARCHAR2(20) not null,
  BRID NUMBER(18) not null,
  YJRQ DATE not null,
  ZBLB NUMBER(4) default 0,
  KSDM VARCHAR2(18) not null,
  YSDM VARCHAR2(10),
  GHBZ NUMBER(1) default 0 not null,
  CZGH VARCHAR2(10)
);

comment on table MS_GHYJ
  is '门诊_挂号预检';
-- Add comments to the columns 
comment on column MS_GHYJ.YJXH
  is '预检序号';
comment on column MS_GHYJ.JGID
  is '机构代码';
comment on column MS_GHYJ.BRID
  is '病人ID';
comment on column MS_GHYJ.YJRQ
  is '预检日期';
comment on column MS_GHYJ.ZBLB
  is '值班类别';
comment on column MS_GHYJ.KSDM
  is '科室代码';
comment on column MS_GHYJ.YSDM
  is '医生代码';
comment on column MS_GHYJ.GHBZ
  is '挂号标志 | 0.预检 1.挂号';
comment on column MS_GHYJ.CZGH
  is '操作工号';
alter table MS_GHYJ
  add constraint PK_MS_GHYJ primary key (YJXH)
  using index;

alter table MS_GHMX add CZSJ date;
alter table ZY_CWSZ add JCRQ date;
--2015.1.13 create by gaof for mpi2.2.1
alter table MPI_ADDRESS add ( CREATETIME date ,CREATEUNIT VARCHAR2(16),CREATEUSER VARCHAR2(20),
 LASTMODIFYTIME date, LASTMODIFYUNIT VARCHAR2(16),LASTMODIFYUSER VARCHAR2(20) );
comment on column MPI_ADDRESS.CREATETIME is '创建时间';
comment on column MPI_ADDRESS.CREATEUNIT is '建档机构';
comment on column MPI_ADDRESS.CREATEUSER is '创建人';
comment on column MPI_ADDRESS.LASTMODIFYTIME is '最后修改时间';
comment on column MPI_ADDRESS.LASTMODIFYUNIT is '最后修改机构';
comment on column MPI_ADDRESS.LASTMODIFYUSER is '最后修改人';
alter table MPI_ADDRESS drop column REGION ;

--2015.1.4 create by gaof for 时限时间设置
-- Create table
create table EMR_SXSJ
(
  GZXH NUMBER(18) not null,
  GZMC VARCHAR2(50),
  KSSJ VARCHAR2(50),
  SXSX NUMBER(9),
  WCBL NUMBER(9),
  JGID VARCHAR2(20)
);
-- Add comments to the columns 
comment on column EMR_SXSJ.GZXH
  is '规则序号';
comment on column EMR_SXSJ.GZMC
  is '规则名称';
comment on column EMR_SXSJ.KSSJ
  is '开始事件';
comment on column EMR_SXSJ.SXSX
  is '书写时限';
comment on column EMR_SXSJ.WCBL
  is '完成病历';
comment on column EMR_SXSJ.JGID
  is '机构id';
-- Create/Recreate primary, unique and foreign key constraints 
alter table EMR_SXSJ
  add constraint PK_EMR_SXSJ primary key (GZXH)
  using index;
  
--chenxr---GM-PHIS-------
alter table ms_ghks add LRJKBK varchar2(1) DEFAULT '0';

create table YS_MZ_BLXD01
(
  xdbh NUMBER(9) not null,
  xdmc VARCHAR2(30) not null,
  srdm VARCHAR2(6) not null,
  xdlb VARCHAR2(20) not null,
  sslb NUMBER(1) not null,
  ssdm VARCHAR2(18),
  gljb NUMBER(18) not null,
  zfbz NUMBER(1) not null
);
comment on table YS_MZ_BLXD01
  is '病历(书写)向导';
comment on column YS_MZ_BLXD01.xdbh
  is '向导编号 主键';
comment on column YS_MZ_BLXD01.xdmc
  is '向导名称';
comment on column YS_MZ_BLXD01.srdm
  is '输入代码';
comment on column YS_MZ_BLXD01.xdlb
  is '向导类别 [病人主诉/简要病史/体格检查]';
comment on column YS_MZ_BLXD01.sslb
  is '所属类别 1 个人 2 科室 3全院';
comment on column YS_MZ_BLXD01.ssdm
  is '所属代码 :1个人 为员工代码 2科室 为科室代码  3全院 为空';
comment on column YS_MZ_BLXD01.gljb
  is '关联疾病 (select * from gy_jbbm where jbxh = [GLJB])';
comment on column YS_MZ_BLXD01.zfbz
  is '作废标志 1 是 0 否';
alter table YS_MZ_BLXD01
  add constraint PK_YS_MZ_BLXD01 primary key (XDBH);  

create table YS_MZ_BLXD02
(
  xmbh NUMBER(18) not null,
  xdbh NUMBER(9),
  xmbm VARCHAR2(40) not null,
  xmmc VARCHAR2(100) not null,
  xmnr VARCHAR2(255) not null
);
comment on column YS_MZ_BLXD02.xmbh
  is '主键';
comment on column YS_MZ_BLXD02.xdbh
  is '同01表';
comment on column YS_MZ_BLXD02.xmbm
  is '项目编码';
comment on column YS_MZ_BLXD02.xmmc
  is '项目名称';
comment on column YS_MZ_BLXD02.xmnr
  is '项目内容';
alter table YS_MZ_BLXD02
  add constraint PK_YS_MZ_BLXD02 primary key (XMBH);


alter table MS_BCJL add bqgz VARCHAR2(1000);
comment on column MS_BCJL.bqgz
  is '病情告知';  
alter table MS_BCJL add DYBZ varchar2(1) DEFAULT '0';
comment on column MS_BCJL.dybz
  is '打印标志';  
alter table MS_BCJL add rsbz NUMBER(1);
comment on column MS_BCJL.rsbz
  is '妊娠标志 0:无、1：妊娠期、2：哺乳期';    
alter table MS_BCJL add zz VARCHAR2(1000);
comment on column MS_BCJL.zz
  is '治则（中医科专用）';  
  
 --Add column 左眼视力、右眼视力,左眼矫正视力、右眼矫正视力， 2015-09-07
 alter table  MS_BCJL add  leftVision number(6,2) ;
 alter table  MS_BCJL add  rightVision number(6,2) ;
 alter table  MS_BCJL add  leftCorrectedVision number(6,2) ;
 alter table  MS_BCJL add  rightCorrectedVision number(6,2) ;  
 
alter table YS_MZ_JZLS add sfdy NUMBER(1);
comment on column YS_MZ_JZLS.sfdy
  is '是否打印'; 
  --guol----------------------2016-08-17
create table SQ_JKPG
(
  JKPGID  VARCHAR2(16) not null,
  CARDID  VARCHAR2(30) not null,
  GRBM    VARCHAR2(32),
  EMPIID  VARCHAR2(32),
  XM      VARCHAR2(16),
  XB      VARCHAR2(1),
  CSNY    TIMESTAMP(6),
  NL      VARCHAR2(2),
  GRSH    VARCHAR2(2),
  HJ1     VARCHAR2(2),
  HJ2     VARCHAR2(2),
  XZZ     VARCHAR2(100),
  LXFS    VARCHAR2(20),
  HYZK    VARCHAR2(2),
  HYQT    VARCHAR2(20),
  WHCD    VARCHAR2(2),
  ZY      VARCHAR2(2),
  SG      NUMBER(3),
  TZ      NUMBER(5,1),
  BMI     NUMBER(5,2),
  YW      NUMBER(3),
  YWGM1   VARCHAR2(2),
  YWGM2   VARCHAR2(2),
  YWGM3   VARCHAR2(2),
  YWGM4   VARCHAR2(2),
  YWGM5   VARCHAR2(2),
  YWQT    VARCHAR2(20),
  SLCJ    VARCHAR2(2),
  TLCJ    VARCHAR2(2),
  YYCJ    VARCHAR2(2),
  ZTCJ    VARCHAR2(2),
  ZLCJ    VARCHAR2(2),
  JSCJ    VARCHAR2(2),
  QTCJ    VARCHAR2(2),
  CJMC    VARCHAR2(20),
  SFYB    VARCHAR2(2),
  YXTN    VARCHAR2(2),
  YXRQ    TIMESTAMP(6),
  EXTN    VARCHAR2(2),
  EXRQ    TIMESTAMP(6),
  GXY     VARCHAR2(2),
  GXYRQ   TIMESTAMP(6),
  GXZ     VARCHAR2(2),
  GXZRQ   TIMESTAMP(6),
  GXB     VARCHAR2(2),
  GXBRQ   TIMESTAMP(6),
  NZZ     VARCHAR2(2),
  NZRQ    TIMESTAMP(6),
  XC      VARCHAR2(2),
  XCRQ    TIMESTAMP(6),
  MZ      VARCHAR2(2),
  MZRQ    TIMESTAMP(6),
  FJH     VARCHAR2(2),
  FJHRQ   TIMESTAMP(6),
  MXGY    VARCHAR2(2),
  MXGYRQ  TIMESTAMP(6),
  GYH     VARCHAR2(2),
  GYHRQ   TIMESTAMP(6),
  WY      VARCHAR2(2),
  WYRQ    TIMESTAMP(6),
  WLXSH   VARCHAR2(2),
  WLXRQ   TIMESTAMP(6),
  WYX     VARCHAR2(2),
  WYXRQ   TIMESTAMP(6),
  WCH     VARCHAR2(2),
  WCHRQ   TIMESTAMP(6),
  CXR     VARCHAR2(2),
  CXRQ    TIMESTAMP(6),
  JCY     VARCHAR2(2),
  JCYRQ   TIMESTAMP(6),
  ZXJSB   VARCHAR2(2),
  ZXJSRQ  TIMESTAMP(6),
  EXZL    VARCHAR2(2),
  ZLMC    VARCHAR2(30),
  ZLRQ    TIMESTAMP(6),
  QTMXB   VARCHAR2(30),
  QTMXRQ  TIMESTAMP(6),
  SSHU    VARCHAR2(2),
  SSMC1   VARCHAR2(30),
  SSRQ1   TIMESTAMP(6),
  SSMC2   VARCHAR2(30),
  SSRQ2   TIMESTAMP(6),
  WS      VARCHAR2(2),
  WSMC1   VARCHAR2(30),
  WSRQ1   TIMESTAMP(6),
  WSMC2   VARCHAR2(30),
  WSRQ2   TIMESTAMP(6),
  SX      VARCHAR2(2),
  SXYY1   VARCHAR2(30),
  SXRQ1   TIMESTAMP(6),
  SXYY2   VARCHAR2(30),
  SXRQ2   TIMESTAMP(6),
  JZFQ    VARCHAR2(2),
  JZMQ    VARCHAR2(2),
  JZXJ    VARCHAR2(2),
  JZZN    VARCHAR2(2),
  JZTN    VARCHAR2(2),
  JZGX    VARCHAR2(2),
  JZGXB   VARCHAR2(2),
  JZNZ    VARCHAR2(2),
  JZFA    VARCHAR2(2),
  JZCA    VARCHAR2(2),
  JZGA    VARCHAR2(2),
  JZWA    VARCHAR2(2),
  JZRA    VARCHAR2(2),
  JZZA    VARCHAR2(2),
  JZQT    VARCHAR2(2),
  JZQTMC  VARCHAR2(30),
  WZTN    VARCHAR2(2),
  WZGX    VARCHAR2(2),
  WZGXB   VARCHAR2(2),
  WZNZ    VARCHAR2(2),
  WZFA    VARCHAR2(2),
  WZCA    VARCHAR2(2),
  WZGA    VARCHAR2(2),
  WZWA    VARCHAR2(2),
  WZRA    VARCHAR2(2),
  WZZA    VARCHAR2(2),
  WZQT    VARCHAR2(2),
  WZQTMC  VARCHAR2(30),
  SSTN    VARCHAR2(2),
  WWXZ    VARCHAR2(2),
  LWXZ    VARCHAR2(2),
  SFXY    VARCHAR2(2),
  ESXY    VARCHAR2(2),
  GLYJ    VARCHAR2(2),
  GYYS    VARCHAR2(2),
  GLYD    VARCHAR2(2),
  JZSC    NUMBER(8),
  BS1     VARCHAR2(2),
  BS2     VARCHAR2(2),
  BS3     VARCHAR2(2),
  BS4     VARCHAR2(2),
  BS5     VARCHAR2(2),
  BS6     VARCHAR2(2),
  BS7     VARCHAR2(2),
  BS8     VARCHAR2(2),
  BS9     VARCHAR2(2),
  BS10    VARCHAR2(2),
  BS11    VARCHAR2(2),
  BS12    VARCHAR2(2),
  BSMC1   VARCHAR2(50),
  BSMC2   VARCHAR2(50),
  BSMC3   VARCHAR2(50),
  BSMC4   VARCHAR2(50),
  BSMC5   VARCHAR2(50),
  BSMC6   VARCHAR2(50),
  BSMC7   VARCHAR2(50),
  BSMC8   VARCHAR2(50),
  BSMC9   VARCHAR2(50),
  BSMC10  VARCHAR2(50),
  BSMC11  VARCHAR2(50),
  BSMC12  VARCHAR2(50),
  GXJD    VARCHAR2(20),
  LRRQ    TIMESTAMP(6),
  CZJD    VARCHAR2(20),
  CZTD    VARCHAR2(20),
  CZRQ    TIMESTAMP(6),
  CZXM    VARCHAR2(20),
  SZJW    VARCHAR2(20),
  CZMC    VARCHAR2(30),
  SFGM    VARCHAR2(5),
  ZYC     NUMBER(2),
  ZC      NUMBER(2),
  LC      NUMBER(2),
  RL      NUMBER(2),
  ZNS     NUMBER(2),
  MCRS    CHAR(1),
  MCC     TIMESTAMP(6),
  SWLX    CHAR(1),
  SWYY    VARCHAR2(100),
  SWSJ    TIMESTAMP(6),
  CXS     CHAR(10),
  NCSSSS  VARCHAR2(50),
  QT      VARCHAR2(100),
  PGCTIME TIMESTAMP(6),
  SSFS    CHAR(2),
  PGCCS   NUMBER(2),
  PGCCX   VARCHAR2(100),
  PGCGR   VARCHAR2(100),
  PGCSX   VARCHAR2(100),
  YCSYJY  CHAR(10),
  YQGXY   CHAR(2),
  YQTNB   CHAR(2),
  PGS     CHAR(2),
  JCJK    CHAR(2),
  JZBS    NUMBER(1),
  SFCJ    NUMBER(1)
)
alter table SQ_JKPG
  add constraint PK_SQ_JKPG primary key (JKPGID)
  using index 
  tablespace BSPHIS2420_SED
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    minextents 1
    maxextents unlimited
  );
  
  
  --yk_ypcd表增加字段 YPID(药品Id)

alter table yk_ypcd add YPID number(8);
	comment on column yk_ypcd.YPID
	 is '药品Id';


   --添加药品引入表
   create table YK_TYPK_YR
(
  YPXH					NUMBER(18),
  YPID   				VARCHAR2(512),
  CPMC   				VARCHAR2(512),
  SCQYMC    			VARCHAR2(512),
  QYBM 					VARCHAR2(128),
  PZWH	 				VARCHAR2(256),
  DL   					VARCHAR2(64),
  DLM   				NUMBER(2),
  FL   					VARCHAR2(128),
  FLM   				VARCHAR2(64),
  PZZW   				VARCHAR2(128),
  PZYW   				VARCHAR2(128),
  PZMDM     			VARCHAR2(64),
  SGYJ   				VARCHAR2(128),
  SGYJFLM   				VARCHAR2(64),
  JXFLMC   				VARCHAR2(128),
  JXFLM   				VARCHAR2(64),
  GG   					VARCHAR2(1024),
  ZJGGFLM   				VARCHAR2(128),
  ZHXS   				VARCHAR2(64),
  ZHXSBM				 VARCHAR2(128),
  CZBZ	 				VARCHAR2(256),
  ZXBZDW				 VARCHAR2(128),
  ZXZJDW				 VARCHAR2(128),
  CYBZ	 				VARCHAR2(1024),
  CYBZM	 				VARCHAR2(64),
  BZ	 				VARCHAR2(512),
  JKZLY	 				VARCHAR2(64),
  ZLCYR	 				VARCHAR2(64),
  PZSSILXY				 VARCHAR2(32),
  ILXYPZSJ				 VARCHAR2(64),
  YYY	 				VARCHAR2(64),
  DJY	 				VARCHAR2(64),
  DDSCYP				 	VARCHAR2(64),
  TGYZXPJYP				 	VARCHAR2(64),
  GJTPYP				 VARCHAR2(128),
  MGSYP	 				VARCHAR2(64),
  GJZDJKHLYYYPML			 VARCHAR2(64),
  YBKAY		 			VARCHAR2(64),
  YPJJZXGZYP				VARCHAR2(64),
  YPJZCGSDPZ				VARCHAR2(64),
  GLFZYML	 			VARCHAR2(64),
  GLYFSBETYPJYQD			VARCHAR2(64),
  JY09	 				VARCHAR2(16),
  JYXH09	 				VARCHAR2(64),
  JY12	 				VARCHAR2(16),
  JYXH12				 		VARCHAR2(64),
  JYFLM12				 VARCHAR2(64),
  YB09BS				 VARCHAR2(32),
  JYBSJL09				 VARCHAR2(32),
  JYL17	 				VARCHAR2(128),
  BH17	 				VARCHAR2(64),
  YPFL17				 VARCHAR2(128),
  YPFLM17				 VARCHAR2(128),
  FL171	 				VARCHAR2(128),
  FL172	 				VARCHAR2(128),
  FL173	 				VARCHAR2(128),
  FL174	 				VARCHAR2(128),
  BZ17	 				VARCHAR2(512),
  JYL19	 				VARCHAR2(64),
  JY2018	 				VARCHAR2(64),
  JYYSMLID2018			 VARCHAR2(256),
  JYLB2018	 			VARCHAR2(512),
  JYYSMLYPFL2018		 VARCHAR2(512),
  JYTYM2018				VARCHAR2(512),
  JYJXGG2018				 VARCHAR2(1024),
  JYMLBH2018				 VARCHAR2(512),
  JYDQ2018	 			VARCHAR2(512),
  JYYSMLBZ2018			 VARCHAR2(512),
  JYBSBCJL12			 VARCHAR2(512),
  NDYFJHYY	 			VARCHAR2(512),
  AZBYY	 				VARCHAR2(512),
  QHSLYW					VARCHAR2(512),
  GJMYGHYYM					VARCHAR2(512),
  BYY	 				VARCHAR2(512),
  YPCD					NUMBER(18)
);
-- Add comments to the columns
comment on column YK_TYPK_YR.YPXH
  is '药品序号';
comment on column YK_TYPK_YR.YPID
  is '药品ID';
comment on column YK_TYPK_YR.CPMC
  is '产品名称';
comment on column YK_TYPK_YR.SCQYMC
  is '生产企业名称';
comment on column YK_TYPK_YR.QYBM
  is '企业编码';
comment on column YK_TYPK_YR.PZWH
  is '批准文号';
comment on column YK_TYPK_YR.DL
  is '大类';
comment on column YK_TYPK_YR.DLM
  is '大类码';
comment on column YK_TYPK_YR.FL
  is '药理/功效分类';
comment on column YK_TYPK_YR.FLM
  is '药理/功效分类码';
comment on column YK_TYPK_YR.PZZW
  is '品种名（中文）';
comment on column YK_TYPK_YR.PZYW
  is '品种名（英文）';
comment on column YK_TYPK_YR.PZMDM
  is '品种名代码';
comment on column YK_TYPK_YR.SGYJ
  is '酸根盐基';
comment on column YK_TYPK_YR.SGYJFLM
  is '酸根盐基分类码';
comment on column YK_TYPK_YR.JXFLMC
  is '剂型分类名称';
comment on column YK_TYPK_YR.JXFLM
  is '剂型分类码';
comment on column YK_TYPK_YR.GG
  is '规格1';
comment on column YK_TYPK_YR.ZJGGFLM
  is '制剂规格分类码';
comment on column YK_TYPK_YR.ZHXS
  is '转换系数';
comment on column YK_TYPK_YR.ZHXSBM
  is '转换系数编码';
comment on column YK_TYPK_YR.CZBZ
  is '材质包装';
comment on column YK_TYPK_YR.ZXBZDW
  is '最小包装单位';
comment on column YK_TYPK_YR.ZXZJDW
  is '最小制剂单位';
comment on column YK_TYPK_YR.CYBZ
  is '差异备注';
comment on column YK_TYPK_YR.CYBZM
  is '差异备注码';
comment on column YK_TYPK_YR.BZ
  is '备注';
comment on column YK_TYPK_YR.JKZLY
  is '进口专利药';
comment on column YK_TYPK_YR.ZLCYR
  is '专利持有人';
comment on column YK_TYPK_YR.PZSSILXY
  is '批准上市的中国I类新药';
comment on column YK_TYPK_YR.ILXYPZSJ
  is '中国I类新药批准时间';
comment on column YK_TYPK_YR.YYY
  is '原研药';
comment on column YK_TYPK_YR.DJY
  is '低价药';
comment on column YK_TYPK_YR.DDSCYP
  is '定点生产药品';
comment on column YK_TYPK_YR.TGYZXPJYP
  is '通过一致性评价药品';
comment on column YK_TYPK_YR.GJTPYP
  is '国家谈判药品';
comment on column YK_TYPK_YR.MGSYP
  is '免关税药品';
comment on column YK_TYPK_YR.GJZDJKHLYYYPML
  is '国家重点监控合理用药药品目录';
comment on column YK_TYPK_YR.YBKAY
  is '17种医保抗癌药';
comment on column YK_TYPK_YR.YPJJZXGZYP
  is '罕见病药品降价专项工作药品';
comment on column YK_TYPK_YR.YPJZCGSDPZ
  is '国家组织药品集中采购试点品种';

comment on column YK_TYPK_YR.GLFZYML
  is '鼓励仿制药目录';
comment on column YK_TYPK_YR.GLYFSBETYPJYQD
  is '鼓励研发申报儿童药品建议清单';
comment on column YK_TYPK_YR.JY09
  is '09基药';
comment on column YK_TYPK_YR.JYXH09
  is '基药09序号';
comment on column YK_TYPK_YR.JY12
  is '理论12基药';
comment on column YK_TYPK_YR.JYXH12
  is '基药12序号';
comment on column YK_TYPK_YR.JYFLM12
  is '基药12分类名';
comment on column YK_TYPK_YR.YB09BS
  is '医保09标识';
comment on column YK_TYPK_YR.JYBSJL09
  is '09版基药标识记录';
comment on column YK_TYPK_YR.JYL17
  is '医保17版甲乙类';
comment on column YK_TYPK_YR.BH17
  is '医保17版编号';
comment on column YK_TYPK_YR.YPFL17
  is '医保17版药品分类';
comment on column YK_TYPK_YR.YPFLM17
  is '医保17版药品分类代码';
comment on column YK_TYPK_YR.FL171
  is '医保17版一级分类';
comment on column YK_TYPK_YR.FL172
  is '医保17版二级分类';
comment on column YK_TYPK_YR.FL173
  is '医保17版三级分类';
comment on column YK_TYPK_YR.FL174
  is '医保17版四级分类';
comment on column YK_TYPK_YR.BZ17
  is '医保17版备注';
comment on column YK_TYPK_YR.JYL19
  is '医保19版甲乙类';
comment on column YK_TYPK_YR.JY2018
  is '2018版基药';
comment on column YK_TYPK_YR.JYYSMLID2018
  is '2018版基药原始目录ID';
comment on column YK_TYPK_YR.JYLB2018
  is '2018版基药类别';
comment on column YK_TYPK_YR.JYYSMLYPFL2018
  is '2018版基药原始目录药品分类';
comment on column YK_TYPK_YR.JYTYM2018
  is '2018版基药通用名';
comment on column YK_TYPK_YR.JYJXGG2018
  is '2018版基药剂型规格';
comment on column YK_TYPK_YR.JYMLBH2018
  is '2018版基药目录编号';
comment on column YK_TYPK_YR.JYDQ2018
  is '2018版基药地区';
comment on column YK_TYPK_YR.JYYSMLBZ2018
  is '2018版基药原始目录备注';
comment on column YK_TYPK_YR.JYBSBCJL12
  is '12版基药标识补充记录';
comment on column YK_TYPK_YR.NDYFJHYY
  is '耐多药肺结核用药';
comment on column YK_TYPK_YR.AZBYY
  is '艾滋病用药';
comment on column YK_TYPK_YR.QHSLYW
  is '青蒿素类药物';
comment on column YK_TYPK_YR.GJMYGHYYM
  is '国家免疫规划用疫苗';
comment on column YK_TYPK_YR.BYY
  is '避孕药';
comment on column YK_TYPK_YR.YPCD
  is '药品产地';

alter table YK_TYPK_YR
  add constraint PK_YK_TYPK_YR_YPXH primary key (YPXH);

--修改药品产地表字段长度
alter table YK_CDDZ modify CDMC VARCHAR2(512);
alter table YK_CDDZ modify PYDM VARCHAR2(32);
alter table YK_CDDZ modify CDQC VARCHAR2(512);
alter table YK_CDDZ modify WBDM VARCHAR2(32);
--修改药品属性表字段长度
alter table YK_YPSX modify SXMC VARCHAR2(32);
alter table YK_YPSX modify PYDM VARCHAR2(32);
--修改药品通用品库表字段长度
alter table YK_TYPK modify YPGG VARCHAR2(64);
alter table YK_TYPK modify YFGG VARCHAR2(64);
alter table YK_TYPK modify BFGG VARCHAR2(64);
