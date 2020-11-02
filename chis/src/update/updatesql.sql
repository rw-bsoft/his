-- chenxr---------2014-11-25---------------
alter table PSY_PSYCHOSISRECORD add informedconsent VARCHAR2(1);
alter table PSY_PSYCHOSISRECORD add icsign VARCHAR2(30);
alter table PSY_PSYCHOSISRECORD add icdate date;
comment on column PSY_PSYCHOSISRECORD.informedconsent
  is '知情同意';
comment on column PSY_PSYCHOSISRECORD.icsign
  is '签字';
comment on column PSY_PSYCHOSISRECORD.icdate
  is '签字时间';
-- chenxr---------2014-11-26---------------
alter table PSY_PSYCHOSISRECORD add filltabledate date;
alter table PSY_PSYCHOSISRECORD add doctorsign VARCHAR2(20);
alter table PSY_PSYCHOSISRECORD add doctorunit VARCHAR2(20);
comment on column PSY_PSYCHOSISRECORD.filltabledate
  is '填表日期';
comment on column PSY_PSYCHOSISRECORD.doctorsign
  is '医生签字';
comment on column PSY_PSYCHOSISRECORD.doctorunit
  is '医生机构';
--chenxr-----------2014-11-28-------------
alter table MDC_TUMOURCONFIRMED drop column  REVIEWDATE;--评审日期
alter table MDC_TUMOURCONFIRMED drop column REVIEWSPECIALIST;--评审专家
alter table PSY_PSYCHOSISRECORD add familysocialimpact VARCHAR2(20);
comment on column PSY_PSYCHOSISRECORD.familysocialimpact
  is '对家庭社会的影响';  
alter table PSY_PSYCHOSISVISIT add familysocialimpact VARCHAR2(20);
comment on column PSY_PSYCHOSISVISIT.familysocialimpact
  is '对家庭社会的影响';  
--chenxr-----------2015-01-05------------------
alter table MDC_TUMOURCONFIRMED drop column reviewDate;
alter table MDC_TUMOURCONFIRMED drop column reviewSpecialist;
alter table MDC_TUMOURCONFIRMED add cancercase VARCHAR2(1);
alter table MDC_TUMOURCONFIRMED add manadoctorid VARCHAR2(20);
alter table MDC_TUMOURCONFIRMED add manaunitid VARCHAR2(20);
comment on column MDC_TUMOURCONFIRMED.cancercase
  is '癌症情况';
comment on column MDC_TUMOURCONFIRMED.manadoctorid
  is '责任医生';
comment on column MDC_TUMOURCONFIRMED.manaunitid
  is '管辖机构';  
--chenxr------------------2015-01-14----------------
alter table PHQ_HEALTHEDUCATIONCOURSE add numberofparticipants number(8);
comment on column PHQ_HEALTHEDUCATIONCOURSE.numberofparticipants
  is '参加人数';  
  update PHQ_HealthEducationCourse t set t.content = '',t.usedtime='';
  alter table PHQ_HEALTHEDUCATIONCOURSE modify content VARCHAR2(2);
  alter table PHQ_HEALTHEDUCATIONCOURSE modify usedtime VARCHAR2(2);
  --chenxr------------------2015-01-22----------------
alter table PHQ_GENERALCASE add courseid VARCHAR2(16);
alter table PHQ_GENERALCASE add content VARCHAR2(2);
comment on column PHQ_GENERALCASE.courseid
  is '课程编号';
comment on column PHQ_GENERALCASE.content
  is '教育内容';
  
  
  
--zhoubn------------------2015-01-22----------------
alter table CDH_CheckupThreeToSix add TZBQHBHS CHAR(1);
comment on column CDH_CheckupThreeToSix.TZBQHBHS  is '吐字不清或不会说 | 1，通过 2,不通过';
alter table CDH_CheckupThreeToSix add ZYQBRCFJ CHAR(1);
comment on column CDH_CheckupThreeToSix.ZYQBRCFJ  is '总要求别人重复讲 | 1，通过 2,不通过';
alter table CDH_CheckupThreeToSix add JCYSSBSZ CHAR(1);
comment on column CDH_CheckupThreeToSix.JCYSSBSZ  is '经常用手势表示主 | 1，通过 2,不通过';

alter table CDH_CheckupThreeToSix add ILLNESSTYPE CHAR(1);
comment on column CDH_CheckupThreeToSix.ILLNESSTYPE  is '两次随访间患病情况';
alter table CDH_CheckupThreeToSix add PNEUMONIACOUNT number(4);
comment on column CDH_CheckupThreeToSix.PNEUMONIACOUNT  is '肺炎次数';
alter table CDH_CheckupThreeToSix add DIARRHEACOUNT number(4);
comment on column CDH_CheckupThreeToSix.DIARRHEACOUNT  is '腹泻次数';
alter table CDH_CheckupThreeToSix add TRAUMACOUNT number(4);
comment on column CDH_CheckupThreeToSix.TRAUMACOUNT  is '外伤次数';
alter table CDH_CheckupThreeToSix add OTHERCOUNT number(4);
comment on column CDH_CheckupThreeToSix.OTHERCOUNT  is '其他次数';
alter table CDH_CheckupThreeToSix add ILLNESSNAME varchar2(80);
comment on column CDH_CheckupThreeToSix.ILLNESSNAME  is '两次随访其他情况';

--yaosq------------------2015-01-27----------------

alter table CDH_CheckupInOne add kylgbz CHAR(1);
alter table CDH_CheckupInOne add kyglbtz CHAR(1);
alter table CDH_CheckupInOne add tlxwgc CHAR(1);
alter table CDH_CheckupInOne add hwhd NUMBER(5,2);
alter table CDH_CheckupInOne add fywss NUMBER(5,2);
alter table CDH_CheckupInOne add hbqk CHAR(1);
alter table CDH_CheckupInOne modify  guide VARCHAR2(20);

alter table CDH_CheckupOneToTwo add yhhfy CHAR(1);
alter table CDH_CheckupOneToTwo add zrwt CHAR(1);
alter table CDH_CheckupOneToTwo add wcdz CHAR(1);
alter table CDH_CheckupOneToTwo add mfsh CHAR(1);
alter table CDH_CheckupOneToTwo add kyglbtz CHAR(1);
alter table CDH_CheckupOneToTwo add hwhd NUMBER(5,2);
alter table CDH_CheckupOneToTwo add fywss NUMBER(5,2);
alter table CDH_CheckupOneToTwo add hbqk CHAR(1);
alter table CDH_CheckupOneToTwo modify  guide VARCHAR2(20);

alter table mdc_diabetessimilarity add registerDate date;
alter table mdc_diabetessimilarity add registerUser varchar2(20);
alter table mdc_diabetessimilarity add registerUnit varchar2(20);
alter table mdc_diabetessimilarity add fbs1 number(6);
alter table mdc_diabetessimilarity add pbs1 number(6);
alter table mdc_diabetessimilarity add result1 varchar2(1);
alter table mdc_diabetessimilarity add checkUser1 varchar2(20);
alter table mdc_diabetessimilarity add checkDate1 date;
alter table mdc_diabetessimilarity add clinicSymptom1 varchar2(1);
alter table mdc_diabetessimilarity add fbs2 number(6);
alter table mdc_diabetessimilarity add pbs2 number(6);
alter table mdc_diabetessimilarity add result2 varchar2(1);
alter table mdc_diabetessimilarity add checkUser2 varchar2(20);
alter table mdc_diabetessimilarity add checkDate2 date;
alter table mdc_diabetessimilarity add clinicSymptom2 varchar2(1);

  --chenxr------------------2015-02-03----------------
alter table MDC_TUMOURSCREENING add istrace VARCHAR2(1);
alter table MDC_TUMOURSCREENING add tracenorm VARCHAR2(1);
comment on column MDC_TUMOURSCREENING.istrace
  is '是否追踪';
comment on column MDC_TUMOURSCREENING.tracenorm
  is '追踪规范';
  
--yaosq------------------2015-02-04----------------
/*==============================================================*/
/* Table: MDC_DiabetesOGTTRecord                                */
/*==============================================================*/
create table MDC_DiabetesOGTTRecord  (
   OGTTID               VARCHAR2(16)                    not null,
   registerDate         DATE,
   registerUser         VARCHAR2(20),
   registerUnit         VARCHAR2(20),
   nextScreenDate       DATE,
   fbs1                 NUMBER(6,2),
   pbs1                 NUMBER(6,2),
   result1              CHAR,
   checkUser1           VARCHAR2(20),
   checkDate1           DATE,
   riskFactors          VARCHAR2(100),
   fbs2                 NUMBER(6,2),
   pbs2                 NUMBER(6,2),
   result2              CHAR,
   checkUser2           VARCHAR2(20),
   checkDate2           DATE,
   fbs3                 NUMBER(6,2),
   pbs3                 NUMBER(6,2),
   result3              CHAR,
   checkUser3           VARCHAR2(20),
   checkDate3           DATE,
   inputUnit            varchar(20),
   inputDate            date,
   inputUser            varchar(20),
   lastModifyUser       varchar(20),
   lastModifyUnit       varchar(20),
   lastModifyDate       date,
   phrId                VARCHAR2(30),
   empiId               VARCHAR2(32),
   superDiagnose        CHAR,
   superDiagnoseText    VARCHAR2(100),
   clinicSymptom1       CHAR,
   clinicSymptom2       CHAR,
   clinicSymptom3       CHAR,
   dbsCreate            CHAR,
   constraint PK_MDC_DIABETESOGTTRECORD primary key (OGTTID)
);

comment on column MDC_DiabetesOGTTRecord.OGTTID is
'管理编号';

comment on column MDC_DiabetesOGTTRecord.registerDate is
'登记日期';

comment on column MDC_DiabetesOGTTRecord.registerUser is
'登记人';

comment on column MDC_DiabetesOGTTRecord.registerUnit is
'登记单位';

comment on column MDC_DiabetesOGTTRecord.nextScreenDate is
'下次筛查时间';

comment on column MDC_DiabetesOGTTRecord.fbs1 is
'空腹血糖1';

comment on column MDC_DiabetesOGTTRecord.pbs1 is
'餐后血糖1';

comment on column MDC_DiabetesOGTTRecord.result1 is
'结果1';

comment on column MDC_DiabetesOGTTRecord.checkUser1 is
'核实人1';

comment on column MDC_DiabetesOGTTRecord.checkDate1 is
'核实日期1';

comment on column MDC_DiabetesOGTTRecord.riskFactors is
'危险因素';

comment on column MDC_DiabetesOGTTRecord.fbs2 is
'空腹血糖2';

comment on column MDC_DiabetesOGTTRecord.pbs2 is
'餐后血糖2';

comment on column MDC_DiabetesOGTTRecord.result2 is
'结果2';

comment on column MDC_DiabetesOGTTRecord.checkUser2 is
'核实人2';

comment on column MDC_DiabetesOGTTRecord.checkDate2 is
'核实日期2';

comment on column MDC_DiabetesOGTTRecord.fbs3 is
'空腹血糖3';

comment on column MDC_DiabetesOGTTRecord.pbs3 is
'餐后血糖3';

comment on column MDC_DiabetesOGTTRecord.result3 is
'结果3';

comment on column MDC_DiabetesOGTTRecord.checkUser3 is
'核实人3';

comment on column MDC_DiabetesOGTTRecord.checkDate3 is
'核实日期3';

comment on column MDC_DiabetesOGTTRecord.inputUnit is
'录入单位';

comment on column MDC_DiabetesOGTTRecord.inputDate is
'录入日期';

comment on column MDC_DiabetesOGTTRecord.inputUser is
'录入员工';

comment on column MDC_DiabetesOGTTRecord.lastModifyUser is
'最后修改人';

comment on column MDC_DiabetesOGTTRecord.lastModifyUnit is
'最后修改机构';

comment on column MDC_DiabetesOGTTRecord.lastModifyDate is
'最后修改日期';

comment on column MDC_DiabetesOGTTRecord.phrId is
'档案编号';

comment on column MDC_DiabetesOGTTRecord.empiId is
'empiId';

comment on column MDC_DiabetesOGTTRecord.superDiagnose is
'上级医院诊断';

comment on column MDC_DiabetesOGTTRecord.superDiagnoseText is
'诊断内容';

comment on column MDC_DiabetesOGTTRecord.clinicSymptom1 is
'有临床症状1';

comment on column MDC_DiabetesOGTTRecord.clinicSymptom2 is
'有临床症状2';

comment on column MDC_DiabetesOGTTRecord.clinicSymptom3 is
'有临床症状3';

comment on column MDC_DiabetesOGTTRecord.dbsCreate is
'建档标志';

--yaosq------------------2015-02-25----------------
alter table CDH_CheckupOneToTwo modify  kyglbtz VARCHAR2(2);
   --zhouw------------------2015-03-02----------------
/*==============================================================*/
/* Table: CDH_CHILDVISITINFO                                */
/*==============================================================*/
create table CDH_CHILDVISITINFO
(
  babyid                  CHAR(16) not null,
  babysex                 CHAR(1),
  babybirth               DATE,
  babyidcard              VARCHAR2(18),
  certificateno           VARCHAR2(16),
  babyaddress             VARCHAR2(100),
  fathername              VARCHAR2(32),
  fatherjob               VARCHAR2(20),
  fatherphone             VARCHAR2(25),
  fatherbirth             DATE,
  mothername              VARCHAR2(32),
  motherjob               VARCHAR2(20),
  motherphone             VARCHAR2(25),
  motherbirth             DATE,
  gestation               NUMBER,
  pregnancydisease        CHAR(1),
  otherdisease            VARCHAR2(30),
  deliveryunit            VARCHAR2(70),
  empiid                  CHAR(32) not null,
  birthstatus             VARCHAR2(10),
  otherstatus             VARCHAR2(30),
  asphyxia                CHAR(1),
  apgar1                  VARCHAR2(10),
  apgar5                  VARCHAR2(10),
  apgarnew                CHAR(3),
  malformation            CHAR(1),
  malformationdescription VARCHAR2(30),
  hearingtest             CHAR(1),
  illnessscreening        CHAR(1),
  otherillness            VARCHAR2(50),
  weight                  NUMBER(5,2),
  length                  NUMBER(5,2),
  inputdate               TIMESTAMP(6),
  inputuser               VARCHAR2(20),
  inputunit               VARCHAR2(20),
  visitdoctor             VARCHAR2(20),
  babyname                VARCHAR2(30)
);
-- Add comments to the columns 
comment on column CDH_CHILDVISITINFO.babysex
  is '性别';
comment on column CDH_CHILDVISITINFO.babybirth
  is '出生日期';
comment on column CDH_CHILDVISITINFO.babyidcard
  is '身份证号码';
comment on column CDH_CHILDVISITINFO.certificateno
  is '出生证号';
comment on column CDH_CHILDVISITINFO.babyaddress
  is '家庭住址';
comment on column CDH_CHILDVISITINFO.gestation
  is '出生孕周';
comment on column CDH_CHILDVISITINFO.pregnancydisease
  is '母亲妊娠期患病情况';
comment on column CDH_CHILDVISITINFO.otherdisease
  is '妊娠期其他疾病';
comment on column CDH_CHILDVISITINFO.deliveryunit
  is '助产机构名称';
comment on column CDH_CHILDVISITINFO.birthstatus
  is '出生情况';
comment on column CDH_CHILDVISITINFO.otherstatus
  is '其它出生情况';
comment on column CDH_CHILDVISITINFO.asphyxia
  is '新生儿窒息';
comment on column CDH_CHILDVISITINFO.apgar1
  is 'Apgar评分1';
comment on column CDH_CHILDVISITINFO.apgar5
  is 'Apgar评分5';
comment on column CDH_CHILDVISITINFO.apgarnew
  is 'Apgar评分(不详)';
comment on column CDH_CHILDVISITINFO.malformation
  is '是否有畸型';
comment on column CDH_CHILDVISITINFO.malformationdescription
  is '畸形描述';
comment on column CDH_CHILDVISITINFO.hearingtest
  is '新生儿听力筛查';
comment on column CDH_CHILDVISITINFO.illnessscreening
  is '新生儿疾病筛查';
comment on column CDH_CHILDVISITINFO.otherillness
  is '其他遗传代谢病';
comment on column CDH_CHILDVISITINFO.weight
  is '出生体重';
comment on column CDH_CHILDVISITINFO.length
  is '出生身高';
comment on column CDH_CHILDVISITINFO.inputdate
  is '录入日期';
comment on column CDH_CHILDVISITINFO.inputuser
  is '录入员工';
comment on column CDH_CHILDVISITINFO.inputunit
  is '录入单位';
comment on column CDH_CHILDVISITINFO.babyname
  is '婴儿姓名';
    --zhouw------------------2015-03-02----------------
/*==============================================================*/
/* Table: CDH_CHILDVISITRECORD                                */
/*==============================================================*/
create table CDH_CHILDVISITRECORD
(
  visitid              CHAR(16) not null,
  empiid               CHAR(32) not null,
  weightnow            NUMBER(5,2),
  feedway              CHAR(1),
  eatnum               NUMBER(20),
  eatcount             NUMBER(5),
  vomit                CHAR(1),
  stoolstatus          CHAR(1),
  stooltimes           NUMBER(5),
  temperature          NUMBER(4,2),
  pulse                INTEGER,
  respiratoryfrequency INTEGER,
  face                 CHAR(1),
  faceother            VARCHAR2(30),
  jaundice             CHAR(1),
  bregmatransverse     NUMBER(5,2),
  bregmalongitudinal   NUMBER(5,2),
  bregmastatus         CHAR(1),
  otherstatus1         VARCHAR2(30),
  eye                  VARCHAR2(2),
  eyeabnormal          VARCHAR2(30),
  limbs                CHAR(1),
  limbsabnormal        VARCHAR2(30),
  ear                  CHAR(1),
  earabnormal          VARCHAR2(30),
  neck                 CHAR(1),
  neckstatus           VARCHAR2(30),
  nose                 VARCHAR2(2),
  noseabnormal         VARCHAR2(30),
  skin                 VARCHAR2(2),
  skinabnormal         VARCHAR2(30),
  mouse                VARCHAR2(2),
  mouseabnormal        VARCHAR2(30),
  anal                 CHAR(1),
  analabnormal         VARCHAR2(30),
  heartlung            CHAR(1),
  heartlungabnormal    VARCHAR2(30),
  genitalia            CHAR(1),
  genitaliaabnormal    VARCHAR2(30),
  abdominal            CHAR(1),
  abdominalabnormal    VARCHAR2(30),
  spine                CHAR(1),
  spineabnormal        VARCHAR2(30),
  umbilical            CHAR(1),
  umbilicalother       VARCHAR2(30),
  referral             CHAR(1),
  referralunit         VARCHAR2(50),
  referralreason       VARCHAR2(50),
  guide                VARCHAR2(64),
  visitdate            DATE,
  nextvisitaddress     VARCHAR2(100),
  nextvisitdate        DATE,
  visitdoctor          VARCHAR2(20),
  neck1                VARCHAR2(30),
  babyid               VARCHAR2(16) not null,
  visitunit            VARCHAR2(20)
);
-- Add comments to the table 
comment on table CDH_CHILDVISITRECORD
  is '儿童访视记录';
-- Add comments to the columns 
comment on column CDH_CHILDVISITRECORD.visitid
  is '随访的序号';
comment on column CDH_CHILDVISITRECORD.weightnow
  is '目前体重';
comment on column CDH_CHILDVISITRECORD.feedway
  is '喂养方式';
comment on column CDH_CHILDVISITRECORD.eatnum
  is '吃奶量';
comment on column CDH_CHILDVISITRECORD.eatcount
  is '吃奶次数';
comment on column CDH_CHILDVISITRECORD.vomit
  is '呕吐';
comment on column CDH_CHILDVISITRECORD.stoolstatus
  is '大便性状';
comment on column CDH_CHILDVISITRECORD.stooltimes
  is '大便次数';
comment on column CDH_CHILDVISITRECORD.temperature
  is '体温';
comment on column CDH_CHILDVISITRECORD.pulse
  is '脉率';
comment on column CDH_CHILDVISITRECORD.respiratoryfrequency
  is '呼吸频率';
comment on column CDH_CHILDVISITRECORD.face
  is '面色';
comment on column CDH_CHILDVISITRECORD.faceother
  is '其它面色';
comment on column CDH_CHILDVISITRECORD.jaundice
  is '黄疸部位';
comment on column CDH_CHILDVISITRECORD.bregmatransverse
  is '前囟纵径';
comment on column CDH_CHILDVISITRECORD.bregmalongitudinal
  is '前囟横径';
comment on column CDH_CHILDVISITRECORD.bregmastatus
  is '前囟状态';
comment on column CDH_CHILDVISITRECORD.otherstatus1
  is '其它前囟状态';
comment on column CDH_CHILDVISITRECORD.eye
  is '眼';
comment on column CDH_CHILDVISITRECORD.eyeabnormal
  is '眼其他异常';
comment on column CDH_CHILDVISITRECORD.limbs
  is '四肢活动';
comment on column CDH_CHILDVISITRECORD.limbsabnormal
  is '四肢活动异常';
comment on column CDH_CHILDVISITRECORD.ear
  is '耳';
comment on column CDH_CHILDVISITRECORD.earabnormal
  is '耳其他异常';
comment on column CDH_CHILDVISITRECORD.neck
  is '颈部包块';
comment on column CDH_CHILDVISITRECORD.neckstatus
  is '颈部包块描述';
comment on column CDH_CHILDVISITRECORD.nose
  is '鼻';
comment on column CDH_CHILDVISITRECORD.noseabnormal
  is '鼻异常';
comment on column CDH_CHILDVISITRECORD.skin
  is '皮肤';
comment on column CDH_CHILDVISITRECORD.skinabnormal
  is '皮肤其它症状';
comment on column CDH_CHILDVISITRECORD.mouse
  is '口腔';
comment on column CDH_CHILDVISITRECORD.mouseabnormal
  is '口腔其他异常';
comment on column CDH_CHILDVISITRECORD.anal
  is '肛门';
comment on column CDH_CHILDVISITRECORD.analabnormal
  is '肛门异常';
comment on column CDH_CHILDVISITRECORD.heartlung
  is '心肺听诊';
comment on column CDH_CHILDVISITRECORD.heartlungabnormal
  is '心肺异常';
comment on column CDH_CHILDVISITRECORD.genitalia
  is '外生殖器';
comment on column CDH_CHILDVISITRECORD.genitaliaabnormal
  is '外生殖器异常';
comment on column CDH_CHILDVISITRECORD.abdominal
  is '腹部';
comment on column CDH_CHILDVISITRECORD.abdominalabnormal
  is '腹部其他异常';
comment on column CDH_CHILDVISITRECORD.spine
  is '脊柱';
comment on column CDH_CHILDVISITRECORD.spineabnormal
  is '脊柱异常';
comment on column CDH_CHILDVISITRECORD.umbilical
  is '脐带';
comment on column CDH_CHILDVISITRECORD.umbilicalother
  is '脐带其它';
comment on column CDH_CHILDVISITRECORD.referral
  is '是否转诊';
comment on column CDH_CHILDVISITRECORD.referralunit
  is '转诊机构及科室';
comment on column CDH_CHILDVISITRECORD.referralreason
  is '转诊原因';
comment on column CDH_CHILDVISITRECORD.guide
  is '指导';
comment on column CDH_CHILDVISITRECORD.visitdate
  is '本次访视日期';
comment on column CDH_CHILDVISITRECORD.nextvisitaddress
  is '下次随访地点';
comment on column CDH_CHILDVISITRECORD.nextvisitdate
  is '下次随访日期';
comment on column CDH_CHILDVISITRECORD.visitdoctor
  is '随访医生';
comment on column CDH_CHILDVISITRECORD.babyid
  is '随访基本信息的id';
comment on column CDH_CHILDVISITRECORD.visitunit
  is '随访单位';
    --zhouw------------------2015-03-03----------------
  alter table MHC_BabyVisitInfo modify BIRTHSTATUS varchar2(10);
  alter table CDH_CHILDVISITINFO modify BIRTHSTATUS varchar2(10);
  alter table MHC_BabyVisitInfo add APGAR1 varchar2(10);
  alter table MHC_BabyVisitInfo add APGAR5 varchar2(10);
      --yaosq------------------2015-03-05----------------
  alter table CDH_DefectRegister modify defectDiagnose varchar2(100);
  alter table MOV_EHR add movesub varchar2(1);
  alter table MDC_DiabetesVisit add targetBmi NUMBER(6,2);
       --zhouw------------------2015-03-05----------------
    alter table CDH_CHILDVISITINFO add FATHEREMPIID  varchar2(32);
  alter table CDH_CHILDVISITINFO add MOTHEREMPIID  varchar2(32);
  -- chenxr---------2015-03-09---------------
alter table MDC_TUMOURSCREENING add screeningunit VARCHAR2(20);
comment on column MDC_TUMOURSCREENING.screeningunit
  is '初筛机构';
alter table MDC_TUMOURSCREENINGCHECKRESULT add criteriontype VARCHAR2(1);
comment on column MDC_TUMOURSCREENINGCHECKRESULT.criteriontype
  is '项目性质';
    
--yaosq------------------2015-03-09----------------
alter table MDC_HypertensionRiskAssessment add dataSource CHAR(1);
alter table MDC_HypertensionRiskAssessment add otherDataSource VARCHAR2(20);
alter table MDC_HypertensionRiskAssessment add hospital CHAR(1);
alter table MDC_HypertensionRiskAssessment add visitEffect CHAR(1);
alter table MDC_HypertensionRiskAssessment add stopDate timestamp;
alter table MDC_HypertensionRiskAssessment add stopCause CHAR(1);

create table PUB_PelpleHealthDiagnose  (
   diagnoseId           VARCHAR2(16)                    not null,
   recordId             VARCHAR2(16)                    not null,
   ICD10                VARCHAR2(50),
   diagnoseName         VARCHAR2(200),
   diagnoseNamePy       VARCHAR2(100),
   diagnoseType         VARCHAR2(20),
   inputUnit            VARCHAR2(20),
   inputDate            DATE,
   inputUser            VARCHAR2(20),
   lastModifyUser       VARCHAR2(20),
   lastModifyUnit       VARCHAR2(20),
   lastModifyDate       DATE,
   constraint PK_PUB_PELPLEHEALTHDIAGNOSE primary key (diagnoseId)
);

comment on table PUB_PelpleHealthDiagnose is
'健康教育诊断信息';

comment on column PUB_PelpleHealthDiagnose.diagnoseId is
'疾病序号';

comment on column PUB_PelpleHealthDiagnose.recordId is
'处方序号';

comment on column PUB_PelpleHealthDiagnose.ICD10 is
'ICD10';

comment on column PUB_PelpleHealthDiagnose.diagnoseName is
'疾病名称';

comment on column PUB_PelpleHealthDiagnose.diagnoseNamePy is
'疾病名称拼音码';

comment on column PUB_PelpleHealthDiagnose.diagnoseType is
'疾病类别';

comment on column PUB_PelpleHealthDiagnose.inputUnit is
'录入单位';

comment on column PUB_PelpleHealthDiagnose.inputDate is
'录入日期';

comment on column PUB_PelpleHealthDiagnose.inputUser is
'录入员工';

comment on column PUB_PelpleHealthDiagnose.lastModifyUser is
'最后修改人';

comment on column PUB_PelpleHealthDiagnose.lastModifyUnit is
'最后修改机构';

comment on column PUB_PelpleHealthDiagnose.lastModifyDate is
'最后修改日期';

alter table HER_HealthRecipeRecord add diagnoseId VARCHAR2(16);
alter table HER_HealthRecipeRecord_ZLSF add diagnoseId VARCHAR2(16);
alter table HER_HealthRecipeRecord_TNBSF add diagnoseId VARCHAR2(16);
alter table HER_HealthRecipeRecord_MZ add diagnoseId VARCHAR2(16);
alter table HER_HealthRecipeRecord_JSBSF add diagnoseId VARCHAR2(16);
alter table HER_HealthRecipeRecord_JHZX add diagnoseId VARCHAR2(16);
alter table HER_HealthRecipeRecord_GXYSF add diagnoseId VARCHAR2(16);
alter table CDH_HEALTHCARD modify defectsType varchar2(100);
    --zhouw------------------2015-03-11----------------
  alter table CDH_CHILDVISITRECORD modify EYE varchar2(2);
   alter table CDH_CHILDVISITRECORD modify NOSE varchar2(2);
    alter table CDH_CHILDVISITRECORD modify MOUSE varchar2(2);
     alter table CDH_CHILDVISITRECORD modify SKIN varchar2(2);
--chenxr------------------2015-03-11----------------
alter table MDC_TUMOURHIGHRISK add screeningpositive VARCHAR2(1);
alter table MDC_TUMOURHIGHRISK add screeningsickness VARCHAR2(1);
alter table MDC_TUMOURHIGHRISK add createcarduser VARCHAR2(20);
alter table MDC_TUMOURHIGHRISK add createcardunit VARCHAR2(20);
alter table MDC_TUMOURHIGHRISK add createcarddate TIMESTAMP(6);
alter table MDC_TUMOURHIGHRISK add timelycreation VARCHAR2(1);
comment on column MDC_TUMOURHIGHRISK.screeningpositive
  is '初筛阳性';
comment on column MDC_TUMOURHIGHRISK.screeningsickness
  is '初筛疾病';
comment on column MDC_TUMOURHIGHRISK.createcarduser
  is '建卡医生';
comment on column MDC_TUMOURHIGHRISK.createcardunit
  is '建卡人机构';
comment on column MDC_TUMOURHIGHRISK.createcarddate
  is '建卡时间';
comment on column MDC_TUMOURHIGHRISK.timelycreation
  is '建卡及时';     
  --chenxr------------------2015-03-17----------------
alter table MDC_TUMOURHIGHRISKVISIT add lastvisitdate date;
alter table MDC_TUMOURHIGHRISKVISIT add visitnorm VARCHAR2(1);
comment on column MDC_TUMOURHIGHRISKVISIT.lastvisitdate
  is '上次随访日期';
comment on column MDC_TUMOURHIGHRISKVISIT.visitnorm
  is '随访规范';
    --chenxr------------------2015-03-18----------------
alter table MDC_TUMOURSCREENINGCHECKRESULT add planid VARCHAR2(16);
comment on column MDC_TUMOURSCREENINGCHECKRESULT.planid
  is '随访计划ID';
alter table MDC_TUMOURHIGHRISKVISIT modify checkresult VARCHAR2(500);
alter table MDC_TUMOURHIGHRISKVISIT add checkResultIds VARCHAR2(2000);
comment on column MDC_TUMOURHIGHRISKVISIT.checkResultIds
  is '检查结果ID集';
  
      --zhouw------------------2015-03-20----------------
      -- Create table
create table EHR_RECORD
(
  recordmoveid    CHAR(16) not null,
  personname      CHAR(20),
  sexcode         VARCHAR2(20),
  movetype        CHAR(1),
  familyid        VARCHAR2(20),
  newfamilyid     VARCHAR2(20),
  applyuser       VARCHAR2(20),
  phrid           VARCHAR2(30),
  regioncode      VARCHAR2(25),
  targetarea      VARCHAR2(25),
  manadoctorid    VARCHAR2(20),
  targetdoctor    VARCHAR2(20),
  manaunitid      VARCHAR2(20),
  targetunit      VARCHAR2(20),
  applyreason     VARCHAR2(500),
  applyunit       VARCHAR2(20),
  applydate       TIMESTAMP(6),
  regioncode_text VARCHAR2(200),
  targetarea_text VARCHAR2(200)
);
alter table EHR_RECORD
  add constraint PK_EHR_RECORD_RMID primary key (recordmoveid);
comment on table EHR_RECORD
  is '家庭成员迁移记录';
comment on column EHR_RECORD.recordmoveid
  is '记录序号';
comment on column EHR_RECORD.personname
  is '姓名';
comment on column EHR_RECORD.sexcode
  is '性别';
comment on column EHR_RECORD.movetype
  is '迁移类别（1=添加、2=迁入、3=迁出、4=解除）';
comment on column EHR_RECORD.familyid
  is '原家庭编码';
comment on column EHR_RECORD.newfamilyid
  is '现家庭编码';
comment on column EHR_RECORD.applyuser
  is '操作医生(申请人)';
comment on column EHR_RECORD.phrid
  is '档案编号';
comment on column EHR_RECORD.regioncode
  is '原网格地址';
comment on column EHR_RECORD.targetarea
  is '现网格地址';
comment on column EHR_RECORD.manadoctorid
  is '原责任医生';
comment on column EHR_RECORD.targetdoctor
  is '现责任医生';
comment on column EHR_RECORD.manaunitid
  is '原管辖机构';
comment on column EHR_RECORD.targetunit
  is '现管辖机构';
comment on column EHR_RECORD.applyreason
  is '申请原因';
comment on column EHR_RECORD.applyunit
  is '申请机构';
comment on column EHR_RECORD.applydate
  is '申请日期';
comment on column EHR_RECORD.regioncode_text
  is '原网格地址';
comment on column EHR_RECORD.targetarea_text
  is '现网格地址';
  --chenxr------------------2015-03-25----------------
alter table PSY_PSYCHOSISVISITMEDICINE modify days null;  


 --xuzb------------------2015-03-27----------------
    -- Create table
create table QUALITY_ZK_ZQ
(
  no         VARCHAR2(16) not null,
  sfwc       VARCHAR2(40),
  zqlb       VARCHAR2(40) not null,
  zkjb       VARCHAR2(40) not null,
  zklb       VARCHAR2(40) not null,
  ylzd1      VARCHAR2(40),
  ylzd2      VARCHAR2(40),
  manaunitid VARCHAR2(40) not null,
  wcrq       VARCHAR2(40)
) ;
-- Add comments to the table 
comment on table QUALITY_ZK_ZQ
  is '质控评分标准维护表_周期表';
-- Add comments to the columns 
comment on column QUALITY_ZK_ZQ.no
  is 'ID';
comment on column QUALITY_ZK_ZQ.sfwc
  is '是否完成';
comment on column QUALITY_ZK_ZQ.zqlb
  is '周期类型';
comment on column QUALITY_ZK_ZQ.zkjb
  is '质控级别';
comment on column QUALITY_ZK_ZQ.zklb
  is '质控类别';
comment on column QUALITY_ZK_ZQ.ylzd1
  is '预留字段1';
comment on column QUALITY_ZK_ZQ.ylzd2
  is '预留字段2';
comment on column QUALITY_ZK_ZQ.manaunitid
  is '管辖机构';
comment on column QUALITY_ZK_ZQ.wcrq
  is '完成时间';
-- Create/Recreate primary, unique and foreign key constraints 
alter table QUALITY_ZK_ZQ
  add constraint ZK_ZQ_ID primary key (NO);
alter table QUALITY_ZK_ZQ
  add constraint 四建唯一 unique (ZKJB, ZKLB, MANAUNITID, ZQLB);
    
    ------xuzb----15/3/27--------
    
    -- Create table
create table QUALITY_ZK_SJ
(
 coderno               VARCHAR2(20) not null,
  prartno               VARCHAR2(20) not null,
  qualitydate           DATE,
  ifpf                  VARCHAR2(20),
  ifzk                  VARCHAR2(20),
  zkno                  VARCHAR2(20) default 1,
  visitid               CHAR(16) not null,
  phrid                 VARCHAR2(30),
  empiid                CHAR(32) not null,
  visitdate             DATE not null,
  visitdoctor           VARCHAR2(20),
  visitunit             VARCHAR2(20),
  visitway              CHAR(1),
  visiteffect           CHAR(1),
  novisitreason         VARCHAR2(100),
  risklevel             CHAR(1),
  hypertensiongroup     VARCHAR2(2),
  constriction          INTEGER,
  diastolic             INTEGER,
  treateffect           VARCHAR2(2),
  weight                NUMBER(8,2),
  targetweight          NUMBER(8,2),
  heartrate             INTEGER,
  targetheartrate       INTEGER,
  othersigns            VARCHAR2(20),
  smokecount            INTEGER,
  targetsmokecount      INTEGER,
  drinkcount            INTEGER,
  targetdrinkcount      INTEGER,
  traintimesweek        INTEGER,
  targettraintimesweek  INTEGER,
  trainminute           INTEGER,
  targettrainminute     INTEGER,
  salt                  INTEGER,
  targetsalt            INTEGER,
  psychologychange      CHAR(1),
  obeydoctor            CHAR(1),
  auxiliarycheck        VARCHAR2(50),
  visitevaluate         CHAR(1),
  medicine              CHAR(1),
  nextdate              DATE,
  complication          VARCHAR2(64),
  currentsymptoms       VARCHAR2(64),
  incorrectmedicine     VARCHAR2(64),
  nomedicine            VARCHAR2(64),
  otherreason           VARCHAR2(100),
  healthrecipe          VARCHAR2(64),
  acceptdegree          CHAR(1),
  nonmedicineway        VARCHAR2(64),
  riskiness             VARCHAR2(64),
  targethurt            VARCHAR2(64),
  cardiovascularevent   CHAR(1),
  manaunitid            VARCHAR2(20),
  manadoctorid          VARCHAR2(20),
  inputunit             VARCHAR2(20),
  inputdate             DATE,
  inputuser             VARCHAR2(20),
  drinktypecode         VARCHAR2(64),
  loseweight            CHAR(1),
  waistline             NUMBER(6,2),
  bmi                   NUMBER(6,2),
  othersymptoms         VARCHAR2(64),
  referralreason        VARCHAR2(64),
  agencyanddept         VARCHAR2(64),
  medicinebadeffect     CHAR(1),
  lateinput             CHAR(1),
  lastmodifyuser        VARCHAR2(20),
  lastmodifyunit        VARCHAR2(20),
  lastmodifydate        TIMESTAMP(6),
  diet                  CHAR(1),
  complicationincrease  CHAR(1),
  medicinebadeffecttext VARCHAR2(200),
  targetbmi             NUMBER(6,2)
  
);
-- Add comments to the columns 
comment on column QUALITY_ZK_SJ.prartno
  is '质控主表id';
comment on column QUALITY_ZK_SJ.coderno
  is '本表ID  coderNo';
comment on column QUALITY_ZK_SJ.qualitydate
  is '质控日期';
comment on column QUALITY_ZK_SJ.ifpf
  is '是否评分';
comment on column QUALITY_ZK_SJ.ifzk
  is '是否质控';
comment on column QUALITY_ZK_SJ.zkno
  is '质控数据ID(默认1表示为原随访数据)';
-- Create/Recreate primary, unique and foreign key constraints 
alter table QUALITY_ZK_SJ
  add constraint PK_SQ_GXSF2 primary key (CODERNO);
alter table QUALITY_ZK_SJ
  add constraint 唯一_PRARTNO_ZKNO_VISITID unique (PRARTNO, ZKNO, VISITID);
  ------xuzb----15/3/27--------

-- Create table
create table QUALITY_ZK
(
  xmlb  VARCHAR2(20) not null,
  xmzlb VARCHAR2(20),
  xmmc  VARCHAR2(40) not null,
  xmbs  VARCHAR2(40) not null,
  id    VARCHAR2(16) not null,
  zfs   VARCHAR2(10),
  bzms  VARCHAR2(500),
  kxx   VARCHAR2(500)
);
-- Add comments to the table 
comment on table QUALITY_ZK
  is '质控评分标准维护表';
-- Add comments to the columns 
comment on column QUALITY_ZK.xmlb
  is '项目类别';
comment on column QUALITY_ZK.xmzlb
  is '项目子类别';
comment on column QUALITY_ZK.xmmc
  is '项目名称';
comment on column QUALITY_ZK.xmbs
  is '项目标识';
comment on column QUALITY_ZK.id
  is 'id';
comment on column QUALITY_ZK.zfs
  is '总分数';
comment on column QUALITY_ZK.bzms
  is '标准描述';
comment on column QUALITY_ZK.kxx
  is '可选项';
-- Create/Recreate primary, unique and foreign key constraints 
alter table QUALITY_ZK
  add constraint ID2 primary key (ID);
alter table QUALITY_ZK
  add constraint 类别与名称组合唯一 unique (XMLB, XMMC);

----  ------xuzb----15/3/27--------
-- Create table
create table QUALITY_ZK_GXSD
(
  recodid VARCHAR2(16) not null,
  xmxh    VARCHAR2(10),
  gxsd    VARCHAR2(10) not null,
  sjcz    VARCHAR2(10) not null,
  df      VARCHAR2(10) not null,
  ylzd2   VARCHAR2(10),
  xmbs    VARCHAR2(40) not null
);
-- Add comments to the table 
comment on table QUALITY_ZK_GXSD
  is '质控评分标准维护表_得分设定';
-- Add comments to the columns 
comment on column QUALITY_ZK_GXSD.recodid
  is 'ID';
comment on column QUALITY_ZK_GXSD.xmxh
  is '项目序号';
comment on column QUALITY_ZK_GXSD.gxsd
  is '关系设定';
comment on column QUALITY_ZK_GXSD.sjcz
  is '数据差值';
comment on column QUALITY_ZK_GXSD.df
  is '得分';
comment on column QUALITY_ZK_GXSD.ylzd2
  is '预留字段2';
comment on column QUALITY_ZK_GXSD.xmbs
  is '项目标识';
-- Create/Recreate primary, unique and foreign key constraints 
alter table QUALITY_ZK_GXSD
  add constraint RECODID primary key (RECODID);
alter table QUALITY_ZK_GXSD
  add constraint 三项唯一 unique (GXSD, SJCZ, XMXH);
  
  
alter table CDH_CheckupOneToTwo modify fywss NUMBER(6,2);

alter table CDH_CHECKUPTHREETOSIX modify DEVEVALUATION VARCHAR2(20);
alter table CDH_CHECKUPTHREETOSIX modify ILLNESSTYPE VARCHAR2(20);
alter table CDH_INQUIRE modify ILLNESSTYPE VARCHAR2(20);

----  ------yaosq----15/4/3--------
/*==============================================================*/
/* Table: MDC_DiabetesYearAssess                                */
/*==============================================================*/
create table MDC_DiabetesYearAssess  (
   recordId             VARCHAR2(16)                    not null,
   empiId               VARCHAR2(32)                    not null,
   phrId                VARCHAR2(30)                    not null,
   fixDate              DATE,
   diabetesGroup        VARCHAR2(2),
   oldGroup             VARCHAR2(2),
   visitCount           INT,
   normManage           CHAR,
   inputUnit            varchar(20),
   inputDate            date,
   inputUser            varchar(20),
   lastModifyUser       varchar(20),
   lastModifyUnit       varchar(20),
   lastModifyDate       date,
   constraint PK_MDC_DIABETESYEARASSESS primary key (recordId)
);

comment on table MDC_DiabetesYearAssess is
'糖尿病年度评估';

comment on column MDC_DiabetesYearAssess.recordId is
'评估序号';

comment on column MDC_DiabetesYearAssess.empiId is
'empiId';

comment on column MDC_DiabetesYearAssess.phrId is
'档案编号';

comment on column MDC_DiabetesYearAssess.fixDate is
'定转组日期';

comment on column MDC_DiabetesYearAssess.diabetesGroup is
'分组';

comment on column MDC_DiabetesYearAssess.oldGroup is
'原组别';

comment on column MDC_DiabetesYearAssess.visitCount is
'随访次数';

comment on column MDC_DiabetesYearAssess.normManage is
'规范管理';

comment on column MDC_DiabetesYearAssess.inputUnit is
'录入单位';

comment on column MDC_DiabetesYearAssess.inputDate is
'录入日期';

comment on column MDC_DiabetesYearAssess.inputUser is
'录入员工';

comment on column MDC_DiabetesYearAssess.lastModifyUser is
'最后修改人';

comment on column MDC_DiabetesYearAssess.lastModifyUnit is
'最后修改机构';

comment on column MDC_DiabetesYearAssess.lastModifyDate is
'最后修改日期';





------zhoubn----15/4/3--------
create table EHR_RECORDINFO  (
  EMPIID CHAR(32) not null,
  GRDA NUMBER(1),
  GRQY NUMBER(1),
  GAO NUMBER(1),
  TANG NUMBER(1),
  LAO NUMBER(1),
  LI NUMBER(1),
  YI NUMBER(1),
  XIAN NUMBER(1),

  YI_DC NUMBER(1),
  XIAN_DC NUMBER(1),
  YI_WEI NUMBER(1),
  XIAN_WEI NUMBER(1),
  YI_GAN NUMBER(1),
  XIAN_GAN NUMBER(1),
  YI_FEI NUMBER(1),
  XIAN_FEI NUMBER(1),
  YI_RX NUMBER(1),
  XIAN_RX NUMBER(1),
  YI_GJ NUMBER(1),
  XIAN_GJ NUMBER(1),  
  
  BAO NUMBER(1),
  JING NUMBER(1),
  ER NUMBER(1),
  RUO NUMBER(1),
  FU NUMBER(1),
  CANZT NUMBER(1),
  CANN NUMBER(1),
  CANZL NUMBER(1),
  LOGOUT NUMBER(1),        
  CREATEUSER         VARCHAR2(20),
  CREATEDATE         TIMESTAMP(6),
  CREATEUNIT         VARCHAR2(20),
  LASTMODIFYUSER     VARCHAR2(20),
  LASTMODIFYDATE     TIMESTAMP(6),
  LASTMODIFYUNIT     VARCHAR2(20),
   
  constraint PK_PUB_EHR_RECORDINFO primary key (EMPIID)
);

comment on table EHR_RECORDINFO
  is '档案信息';
-- Add comments to the columns 
comment on column EHR_RECORDINFO.EMPIID
  is 'EMPIID';
comment on column EHR_RECORDINFO.GRDA
  is '个人档案';
comment on column EHR_RECORDINFO.GRQY
  is '个人签约';
comment on column EHR_RECORDINFO.GAO
  is '高血压档案';
comment on column EHR_RECORDINFO.TANG
  is '糖尿病档案';
comment on column EHR_RECORDINFO.LAO
  is '老年人档案';
comment on column EHR_RECORDINFO.LI
  is '离休干部档案';
comment on column EHR_RECORDINFO.YI
  is '肿瘤易患';
comment on column EHR_RECORDINFO.XIAN
  is '肿瘤现患';
comment on column EHR_RECORDINFO.BAO
  is '肿瘤报告卡';
  

comment on column EHR_RECORDINFO.YI_DC 
  is '大肠易患';
comment on column EHR_RECORDINFO.XIAN_DC   
  is '大肠现患';
comment on column EHR_RECORDINFO.YI_WEI
  is '胃易患';
comment on column EHR_RECORDINFO.XIAN_WEI 
  is '胃现患';
comment on column EHR_RECORDINFO.YI_GAN 
  is '肝易患';
comment on column EHR_RECORDINFO.XIAN_GAN
  is '肝现患';
comment on column EHR_RECORDINFO.YI_FEI 
  is '肺易患';
comment on column EHR_RECORDINFO.XIAN_FEI 
  is '肺现患';
comment on column EHR_RECORDINFO.YI_RX 
  is '乳腺易患';
comment on column EHR_RECORDINFO.XIAN_RX 
  is '乳腺现患';
comment on column EHR_RECORDINFO.YI_GJ 
  is '宫颈易患';
comment on column EHR_RECORDINFO.XIAN_GJ 
  is '宫颈现患';
  
  
comment on column EHR_RECORDINFO.JING
  is '精神病档案';
comment on column EHR_RECORDINFO.ER
  is '儿童档案';
comment on column EHR_RECORDINFO.RUO
  is '体弱儿童档案';
comment on column EHR_RECORDINFO.FU
  is '孕产妇档案';
comment on column EHR_RECORDINFO.CANZT
  is '残疾人肢档案';
comment on column EHR_RECORDINFO.CANN
  is '残疾人脑档案';
comment on column EHR_RECORDINFO.CANZL
  is '残疾人智档案';
comment on column EHR_RECORDINFO.LOGOUT
  is '注销标志';  
  
comment on column EHR_RECORDINFO.CREATEUSER
  is '建档人';
comment on column EHR_RECORDINFO.CREATEDATE
  is '建档日期';
comment on column EHR_RECORDINFO.CREATEUNIT
  is '建档单位';
comment on column EHR_RECORDINFO.LASTMODIFYUSER
  is '最后修改人';
comment on column EHR_RECORDINFO.LASTMODIFYDATE
  is '最后修改日期';
comment on column EHR_RECORDINFO.LASTMODIFYUNIT
  is '最后修改单位';
  
  ------yaosq---15/4/9--------
  alter table MDC_HypertensionRisk add registerDate date;
alter table MDC_HypertensionRisk add registerUser VARCHAR2(20);
alter table MDC_HypertensionRisk add registerUnit VARCHAR2(20);
alter table MDC_HypertensionRisk add createFlag CHAR(1);
alter table MDC_HypertensionRisk add effectCase CHAR(1);
alter table MDC_HypertensionRisk add statusCase CHAR(1);
alter table MDC_HypertensionRisk add otherDataSource VARCHAR2(20);
alter table MDC_HypertensionRisk add hospital CHAR(1);
alter table MDC_HypertensionRisk add stopDate date;
alter table MDC_HypertensionRisk add riskiness VARCHAR2(64);
alter table MDC_HypertensionRisk add age INTEGER;
alter table MDC_HypertensionRisk add weight NUMBER(8,2);
alter table MDC_HypertensionRisk add height NUMBER(8,2);
alter table MDC_HypertensionRisk add bmi NUMBER(8,2);
alter table MDC_HypertensionRisk add waistLine NUMBER(8,2);
alter table MDC_HypertensionRisk add smokeCount INTEGER;
alter table MDC_HypertensionRisk add smokeYears INTEGER;
alter table MDC_HypertensionRisk add drinkTimes INTEGER;
alter table MDC_HypertensionRisk add drinkCount INTEGER;
alter table MDC_HypertensionRisk add saltCount INTEGER;
alter table MDC_HypertensionRisk add tc NUMBER(8,2);
alter table MDC_HypertensionRisk add td NUMBER(8,2);
alter table MDC_HypertensionRisk add ldl NUMBER(8,2);
alter table MDC_HypertensionRisk add hdl NUMBER(8,2);
alter table MDC_HypertensionRisk add fbs NUMBER(8,2);
alter table MDC_HypertensionRisk add pbs NUMBER(8,2);
alter table MDC_HypertensionRisk add cancellationReason CHAR(1);
alter table MDC_HypertensionRisk add deadReason VARCHAR2(50);
alter table MDC_HypertensionRisk add deadDate date;



/*==============================================================*/
/* Table: HER_HEALTHRECIPERECORD_GXYGWSF                        */
/*==============================================================*/
create table HER_HEALTHRECIPERECORD_GXYGWSF  (
   ID                   VARCHAR2(16)                    not null,
   EMPIID               VARCHAR2(32),
   PHRID                VARCHAR2(30),
   EXAMINEUNIT          VARCHAR2(100),
   DIAGNOSENAME         VARCHAR2(200),
   DIAGNOSEID         VARCHAR2(16),
   ICD10                VARCHAR2(50),
   GUIDEDATE            DATE,
   GUIDEUSER            VARCHAR2(20),
   HEALTHTEACH          VARCHAR2(4000),
   GUIDEWAY             VARCHAR2(2),
   INPUTUNIT            VARCHAR2(20),
   INPUTDATE            DATE,
   INPUTUSER            VARCHAR2(20),
   LASTMODIFYUSER       VARCHAR2(20),
   LASTMODIFYUNIT       VARCHAR2(20),
   LASTMODIFYDATE       DATE,
   RECIPENAME           VARCHAR2(200),
   WAYID                VARCHAR2(32),
   RECORDID             VARCHAR2(16),
   constraint PK_HER_HEALTHRECIPE_GXYGWSF primary key (ID)
);

comment on table HER_HEALTHRECIPERECORD_GXYGWSF is
'健康处方维护';

comment on column HER_HEALTHRECIPERECORD_GXYGWSF.ID is
'主键';

comment on column HER_HEALTHRECIPERECORD_GXYGWSF.EMPIID is
'EMPIID';

comment on column HER_HEALTHRECIPERECORD_GXYGWSF.PHRID is
'健康档案编号';

comment on column HER_HEALTHRECIPERECORD_GXYGWSF.EXAMINEUNIT is
'就诊机构';

comment on column HER_HEALTHRECIPERECORD_GXYGWSF.DIAGNOSENAME is
'诊断名称';

comment on column HER_HEALTHRECIPERECORD_GXYGWSF.ICD10 is
'ICD10';

comment on column HER_HEALTHRECIPERECORD_GXYGWSF.GUIDEDATE is
'指导日期';

comment on column HER_HEALTHRECIPERECORD_GXYGWSF.GUIDEUSER is
'指导医生';

comment on column HER_HEALTHRECIPERECORD_GXYGWSF.HEALTHTEACH is
'指导建议';

comment on column HER_HEALTHRECIPERECORD_GXYGWSF.GUIDEWAY is
'指导途径';

comment on column HER_HEALTHRECIPERECORD_GXYGWSF.INPUTUNIT is
'录入单位';

comment on column HER_HEALTHRECIPERECORD_GXYGWSF.INPUTDATE is
'录入日期';

comment on column HER_HEALTHRECIPERECORD_GXYGWSF.INPUTUSER is
'录入员工';

comment on column HER_HEALTHRECIPERECORD_GXYGWSF.LASTMODIFYUSER is
'最后修改人';

comment on column HER_HEALTHRECIPERECORD_GXYGWSF.LASTMODIFYUNIT is
'最后修改机构';

comment on column HER_HEALTHRECIPERECORD_GXYGWSF.LASTMODIFYDATE is
'最后修改日期';

comment on column HER_HEALTHRECIPERECORD_GXYGWSF.RECIPENAME is
'健康处方名称';

comment on column HER_HEALTHRECIPERECORD_GXYGWSF.WAYID is
'业务主键';

comment on column HER_HEALTHRECIPERECORD_GXYGWSF.RECORDID is
'处方主键';


  ------chenxr---15/4/13--------
alter table MDC_TUMOURHIGHRISKCRITERION add psitemrelation VARCHAR2(1);
alter table MDC_TUMOURHIGHRISKCRITERION add traceitemrelation VARCHAR2(1);
comment on column MDC_TUMOURHIGHRISKCRITERION.psitemrelation
  is '初筛检查项目关系';
comment on column MDC_TUMOURHIGHRISKCRITERION.traceitemrelation
  is '追踪检查项目关系';
  ------yaosq---15/4/17--------  
-- Create table
create table MDC_HYPERTENSIONASSESSPARAMETE
(
  RECORDID             VARCHAR2(16) not null,
  ASSESSTYPE           VARCHAR2(1),
  RECORDWRITEOFF       VARCHAR2(5),
  NEWPATIENT           VARCHAR2(5),
  NOTNORMPATIENT       VARCHAR2(5),
  ONEGROUP             VARCHAR2(5),
  ONEGROUPPROPORTION   NUMBER,
  TWOGROUP             VARCHAR2(5),
  TWOGROUPPROPORTION   NUMBER,
  THREEGROUP           VARCHAR2(5),
  THREEGROUPPROPORTION NUMBER,
  ASSESSDAYS           INTEGER,
  ASSESSHOUR1          INTEGER,
  ASSESSHOUR2          INTEGER
);
-- Add comments to the table 
comment on table MDC_HYPERTENSIONASSESSPARAMETE
  is '高血压评估参数设置';
-- Add comments to the columns 
comment on column MDC_HYPERTENSIONASSESSPARAMETE.RECORDID
  is '主键';
comment on column MDC_HYPERTENSIONASSESSPARAMETE.ASSESSTYPE
  is '评估类型';
comment on column MDC_HYPERTENSIONASSESSPARAMETE.RECORDWRITEOFF
  is '排除高血压档案已注销或个人档案已注销病人';
comment on column MDC_HYPERTENSIONASSESSPARAMETE.NEWPATIENT
  is '新病人不评价(维持原组不变)';
comment on column MDC_HYPERTENSIONASSESSPARAMETE.NOTNORMPATIENT
  is '未规范管理的病人不进行年度评估';
comment on column MDC_HYPERTENSIONASSESSPARAMETE.ONEGROUP
  is '一组随访次数最低标准';
comment on column MDC_HYPERTENSIONASSESSPARAMETE.ONEGROUPPROPORTION
  is '一组占计划随访数比例(%)';
comment on column MDC_HYPERTENSIONASSESSPARAMETE.TWOGROUP
  is '二组随访次数最低标准';
comment on column MDC_HYPERTENSIONASSESSPARAMETE.TWOGROUPPROPORTION
  is '二组占计划随访数比例(%)';
comment on column MDC_HYPERTENSIONASSESSPARAMETE.THREEGROUP
  is '三组随访次数最低标准';
comment on column MDC_HYPERTENSIONASSESSPARAMETE.THREEGROUPPROPORTION
  is '三组占计划随访数比例(%)';
-- Create/Recreate primary, unique and foreign key constraints 
alter table MDC_HYPERTENSIONASSESSPARAMETE
  add constraint PK_MDC_HYPERTENSIONASSESSPARAM primary key (RECORDID);


----  ------yaosq----15/4/20--------
/*==============================================================*/
/* Table: MDC_HypertensionYearAssess                                */
/*==============================================================*/
create table MDC_HypertensionYearAssess  (
   recordId             VARCHAR2(16)                    not null,
   empiId               VARCHAR2(32)                    not null,
   phrId                VARCHAR2(30)                    not null,
   fixDate              DATE,
   hypertensionGroup        VARCHAR2(2),
   oldGroup             VARCHAR2(2),
   visitCount           INT,
   normManage           CHAR,
   inputUnit            varchar(20),
   inputDate            date,
   inputUser            varchar(20),
   lastModifyUser       varchar(20),
   lastModifyUnit       varchar(20),
   lastModifyDate       date,
   constraint PK_MDC_HYPERTENSIONYEARASSESS primary key (recordId)
);

comment on table MDC_HypertensionYearAssess is
'高血压年度评估';

comment on column MDC_HypertensionYearAssess.recordId is
'评估序号';

comment on column MDC_HypertensionYearAssess.empiId is
'empiId';

comment on column MDC_HypertensionYearAssess.phrId is
'档案编号';

comment on column MDC_HypertensionYearAssess.fixDate is
'定转组日期';

comment on column MDC_HypertensionYearAssess.hypertensionGroup is
'分组';

comment on column MDC_HypertensionYearAssess.oldGroup is
'原组别';

comment on column MDC_HypertensionYearAssess.visitCount is
'随访次数';

comment on column MDC_HypertensionYearAssess.normManage is
'规范管理';

comment on column MDC_HypertensionYearAssess.inputUnit is
'录入单位';

comment on column MDC_HypertensionYearAssess.inputDate is
'录入日期';

comment on column MDC_HypertensionYearAssess.inputUser is
'录入员工';

comment on column MDC_HypertensionYearAssess.lastModifyUser is
'最后修改人';

comment on column MDC_HypertensionYearAssess.lastModifyUnit is
'最后修改机构';

comment on column MDC_HypertensionYearAssess.lastModifyDate is
'最后修改日期';


--2014.1.20 update by zhoubn for YK_TYPK
alter table YK_TYPK add YYBS number(1);
comment on column YK_TYPK.YYBS  is '用药标识  | 1.高血压、2.糖尿病、3.精神病、4.胰岛素';
alter table YK_TYPK add GMYWLB number(1);
comment on column YK_TYPK.GMYWLB  is '过敏药物类别  | 1.青霉素、2.磺胺、3.链霉素';  


--2015-04-21 --chenxr----------------
alter table CONS_CONSULTATIONRECORD modify phrid VARCHAR2(30);
alter table MDC_DIABETESRISKASSESSMENT modify phrid VARCHAR2(30);
alter table MDC_DIABETESRISKVISIT modify phrid VARCHAR2(30);
alter table MDC_DIABETESSIMILARITY modify phrid VARCHAR2(30);
alter table MDC_HYPERTENSION_FCBP modify phrid VARCHAR2(30);
alter table MDC_HYPERTENSIONRISKASSESSMENT modify phrid VARCHAR2(30);
alter table MDC_HYPERTENSIONRISKVISIT modify phrid VARCHAR2(30);
alter table MDC_HYPERTENSIONSIMILARITY modify phrid VARCHAR2(30);
alter table MDC_HYPERTENSIONSIMILARITYC modify phrid VARCHAR2(30);
alter table INC_INCOMPLETERECORD modify phrid VARCHAR2(30);
alter table MHC_PREGNANTRECORD modify husbandphrid VARCHAR2(30);
alter table MOV_MHC modify phrid VARCHAR2(30);
alter table RVC_RETIREDVETERANCADRESRECORD modify phrid VARCHAR2(30);
alter table RVC_RETIREDVETERANCADRESVISIT modify phrid VARCHAR2(30);
alter table MDC_HYPERTENSIONRISK modify phrid VARCHAR2(30);
alter table PUB_PREGNANTLOG modify phrid CHAR(30);
--2015-04-22 --chenxr----------------
alter table MDC_TUMOURSCREENING add tqdate date;
comment on column MDC_TUMOURSCREENING.tqdate is '问卷日期';
--//
alter table MDC_TUMOURSCREENING add checkitem1 VARCHAR2(100);
alter table MDC_TUMOURSCREENING add checkresult1 VARCHAR2(30);
alter table MDC_TUMOURSCREENING add checkitem2 VARCHAR2(100);
alter table MDC_TUMOURSCREENING add checkresult2 VARCHAR2(30);
alter table MDC_TUMOURSCREENING add checkitem3 VARCHAR2(100);
alter table MDC_TUMOURSCREENING add checkresult3 VARCHAR2(30);
comment on column MDC_TUMOURSCREENING.checkitem1
  is '检查项目1';
comment on column MDC_TUMOURSCREENING.checkresult1
  is '检查结果1';
comment on column MDC_TUMOURSCREENING.checkitem2
  is '检查项目2';
comment on column MDC_TUMOURSCREENING.checkresult2
  is '检查结果2';
comment on column MDC_TUMOURSCREENING.checkitem3
  is '检查项目3';
comment on column MDC_TUMOURSCREENING.checkresult3
  is '检查结果3';  
  
  --2015-04-23 --yaosq----------------

/*==============================================================*/
/* Table: MDC_HYPERTENSIONBPCONTROL                             */
/*==============================================================*/
create table MDC_HYPERTENSIONBPCONTROL  (
   RECORDID             VARCHAR2(16)                    not null,
   GROUPS               VARCHAR2(2),
   VISITCOUNT           NUMBER,
   SBP                  NUMBER,
   XG                   CHAR(10),
   DBP                  NUMBER,
   CONTROLCONDITION     VARCHAR2(1),
   constraint PK_MDC_HYPERTENSIONBPCONTROL primary key (RECORDID)
);

comment on table MDC_HYPERTENSIONBPCONTROL is
'高血压血压控制设置';

comment on column MDC_HYPERTENSIONBPCONTROL.RECORDID is
'主键';

comment on column MDC_HYPERTENSIONBPCONTROL.GROUPS is
'组别1.一组2.二组3.三组';

comment on column MDC_HYPERTENSIONBPCONTROL.VISITCOUNT is
'随访次数';

comment on column MDC_HYPERTENSIONBPCONTROL.SBP is
'血压控制范围（收缩压';

comment on column MDC_HYPERTENSIONBPCONTROL.XG is
'/';

comment on column MDC_HYPERTENSIONBPCONTROL.DBP is
'舒张压）(<=)';

comment on column MDC_HYPERTENSIONBPCONTROL.CONTROLCONDITION is
'控制情况1.控制优良2.控制尚可3.控制不良';


/*==============================================================*/
/* Table: MDC_HYPERTENSIONCONTROL                               */
/*==============================================================*/
create table MDC_HYPERTENSIONCONTROL  (
   RECORDID             VARCHAR2(16)                    not null,
   CONTROLCONDITION     VARCHAR2(1)                     not null,
   VERYHIGHRISK         VARCHAR2(2),
   HIGHRISK             VARCHAR2(2),
   MIDDLERISK           VARCHAR2(2),
   LOWRISK              VARCHAR2(2),
   constraint PK_MDC_HYPERTENSIONCONTROL primary key (RECORDID)
);

comment on table MDC_HYPERTENSIONCONTROL is
'高血压控制情况分组';

comment on column MDC_HYPERTENSIONCONTROL.RECORDID is
'主键';

comment on column MDC_HYPERTENSIONCONTROL.CONTROLCONDITION is
'控制情况:1.控制优良2.控制尚可3.控制不良4.未评价5.新病人';

comment on column MDC_HYPERTENSIONCONTROL.VERYHIGHRISK is
'很高危:1.一组2.二组3.三组';

comment on column MDC_HYPERTENSIONCONTROL.HIGHRISK is
'高危:1.一组2.二组3.三组';

comment on column MDC_HYPERTENSIONCONTROL.MIDDLERISK is
'中危:1.一组2.二组3.三组';

comment on column MDC_HYPERTENSIONCONTROL.LOWRISK is
'低危:1.一组2.二组3.三组';

insert into MDC_HYPERTENSIONBPCONTROL (RECORDID, GROUPS, VISITCOUNT, SBP, XG, DBP, CONTROLCONDITION)
values ('0000000000000001', '01', 10, 140, '/         ', 90, '1');
insert into MDC_HYPERTENSIONBPCONTROL (RECORDID, GROUPS, VISITCOUNT, SBP, XG, DBP, CONTROLCONDITION)
values ('0000000000000002', '01', 7, 140, '/         ', 90, '2');
insert into MDC_HYPERTENSIONBPCONTROL (RECORDID, GROUPS, VISITCOUNT, SBP, XG, DBP, CONTROLCONDITION)
values ('0000000000000003', '02', 4, 140, '/         ', 90, '1');
insert into MDC_HYPERTENSIONBPCONTROL (RECORDID, GROUPS, VISITCOUNT, SBP, XG, DBP, CONTROLCONDITION)
values ('0000000000000004', '02', 3, 140, '/         ', 90, '2');
insert into MDC_HYPERTENSIONBPCONTROL (RECORDID, GROUPS, VISITCOUNT, SBP, XG, DBP, CONTROLCONDITION)
values ('0000000000000005', '03', 2, 140, '/         ', 90, '1');
insert into MDC_HYPERTENSIONBPCONTROL (RECORDID, GROUPS, VISITCOUNT, SBP, XG, DBP, CONTROLCONDITION)
values ('0000000000000006', '03', 1, 140, '/         ', 90, '3');

insert into MDC_HYPERTENSIONCONTROL (RECORDID, CONTROLCONDITION, VERYHIGHRISK, HIGHRISK, MIDDLERISK, LOWRISK)
values ('0000000000000001', '1', '01', '01', '02', '03');
insert into MDC_HYPERTENSIONCONTROL (RECORDID, CONTROLCONDITION, VERYHIGHRISK, HIGHRISK, MIDDLERISK, LOWRISK)
values ('0000000000000002', '2', '01', '01', '02', '03');
insert into MDC_HYPERTENSIONCONTROL (RECORDID, CONTROLCONDITION, VERYHIGHRISK, HIGHRISK, MIDDLERISK, LOWRISK)
values ('0000000000000003', '3', '01', '01', '02', '03');
insert into MDC_HYPERTENSIONCONTROL (RECORDID, CONTROLCONDITION, VERYHIGHRISK, HIGHRISK, MIDDLERISK, LOWRISK)
values ('0000000000000004', '4', '01', '01', '02', '03');
insert into MDC_HYPERTENSIONCONTROL (RECORDID, CONTROLCONDITION, VERYHIGHRISK, HIGHRISK, MIDDLERISK, LOWRISK)
values ('0000000000000005', '5', '01', '01', '02', '03');

alter table MDC_HYPERTENSIONRISKVISIT add VISITUNIT varchar2(20);
alter table MDC_HYPERTENSIONRISKVISIT add stopDate date;
alter table MDC_HYPERTENSIONRISKVISIT add visitEffect CHAR(1);
alter table MDC_HYPERTENSIONRISKVISIT add stopCause CHAR(1);
alter table MDC_HYPERTENSIONRISKVISIT add height NUMBER(8,2);
alter table MDC_HYPERTENSIONRISKVISIT add RISKID CHAR(16);
alter table MDC_DiabetesRiskAssessment add age INTEGER;
alter table MDC_DiabetesRiskAssessment add RISKID VARCHAR2(16);

--//--yaosq-----chexr bu----------
alter table MPI_DEMOGRAPHICINFO add ylzh VARCHAR2(32);
comment on column MPI_DEMOGRAPHICINFO.ylzh
  is '医疗证号';
  
  
alter table EHR_RECORDINFO add BAO NUMBER(1);
comment on column EHR_RECORDINFO.BAO is '肿瘤报告卡';

--2015-04-28--yaosq--------------
alter table HC_AccessoryExamination modify lefteye         NUMBER(10,1);
alter table HC_AccessoryExamination modify righteye        NUMBER(10,1);
alter table HC_AccessoryExamination modify reclefteye      NUMBER(10,1);
alter table HC_AccessoryExamination modify recrighteye     NUMBER(10,1);
alter table HC_AccessoryExamination modify hgb             NUMBER(10,1);
alter table HC_AccessoryExamination modify wbc             NUMBER(10,1);
alter table HC_AccessoryExamination modify platelet        NUMBER(10,1);
alter table HC_AccessoryExamination modify fbs             NUMBER(10,1);
alter table HC_AccessoryExamination modify fbs2            NUMBER(10,1);
alter table HC_AccessoryExamination modify malb            NUMBER(10,1);
alter table HC_AccessoryExamination modify alt             NUMBER(10,1);
alter table HC_AccessoryExamination modify ast             NUMBER(10,1);
alter table HC_AccessoryExamination modify alb             NUMBER(10,1);
alter table HC_AccessoryExamination modify tbil            NUMBER(10,1);
alter table HC_AccessoryExamination modify dbil            NUMBER(10,1);
alter table HC_AccessoryExamination modify cr              NUMBER(10,1);
alter table HC_AccessoryExamination modify bun             NUMBER(10,1);
alter table HC_AccessoryExamination modify kalemia         NUMBER(10,1);
alter table HC_AccessoryExamination modify natremia        NUMBER(10,1);
alter table HC_AccessoryExamination modify tc              NUMBER(10,1);
alter table HC_AccessoryExamination modify tg              NUMBER(10,1);
alter table HC_AccessoryExamination modify ldl             NUMBER(10,1);
alter table HC_AccessoryExamination modify hdl             NUMBER(10,1);



--2015-05-06--zhoubn--------------
alter table ehr_recordinfo add YI_DC NUMBER(1);
comment on column EHR_RECORDINFO.YI_DC is '大肠易患';
alter table ehr_recordinfo add XIAN_DC NUMBER(1);
comment on column EHR_RECORDINFO.XIAN_DC is '大肠现患';
alter table ehr_recordinfo add YI_WEI NUMBER(1);
comment on column EHR_RECORDINFO.YI_WEI is '胃易患';
alter table ehr_recordinfo add XIAN_WEI NUMBER(1);
comment on column EHR_RECORDINFO.XIAN_WEI is '胃现患';
alter table ehr_recordinfo add YI_GAN NUMBER(1);
comment on column EHR_RECORDINFO.YI_GAN is '肝易患';
alter table ehr_recordinfo add XIAN_GAN NUMBER(1);
comment on column EHR_RECORDINFO.XIAN_GAN is '肝现患';
alter table ehr_recordinfo add YI_FEI NUMBER(1);
comment on column EHR_RECORDINFO.YI_FEI is '肺易患';
alter table ehr_recordinfo add XIAN_FEI NUMBER(1);
comment on column EHR_RECORDINFO.XIAN_FEI is '肺现患';
alter table ehr_recordinfo add YI_RX NUMBER(1);
comment on column EHR_RECORDINFO.YI_RX is '乳腺易患';
alter table ehr_recordinfo add XIAN_RX NUMBER(1);
comment on column EHR_RECORDINFO.XIAN_RX is '乳腺现患';
alter table ehr_recordinfo add YI_GJ NUMBER(1);
comment on column EHR_RECORDINFO.YI_GJ is '宫颈易患';
alter table ehr_recordinfo add XIAN_GJ NUMBER(1);
comment on column EHR_RECORDINFO.XIAN_GJ is '宫颈现患';

--2015-05-08--yaosq--------------
alter table MDC_DiabetesSimilarity add sourceId VARCHAR2(32);
comment on column MDC_DiabetesSimilarity.sourceId is '来源主键';
--2015-05-13--chenxr----------------------
alter table MDC_TUMOURSCREENING add questionnairepositive VARCHAR2(1);
alter table MDC_TUMOURSCREENING add checkpositive VARCHAR2(1);
alter table MDC_TUMOURSCREENING add syntheticalpositive VARCHAR2(1);
comment on column MDC_TUMOURSCREENING.questionnairepositive
  is '问卷阳性(0:阴性 1:阳性)';
comment on column MDC_TUMOURSCREENING.checkpositive
  is '检查阳性(0:阴性 1:阳性)';
comment on column MDC_TUMOURSCREENING.syntheticalpositive
  is '综合阳性(0:阴性 1:阳性)';
  
  --2015-05-15--yaosq--------------
alter table MDC_HypertensionVisit add medicineNot VARCHAR2(2);
alter table MDC_HypertensionVisit add medicineOtherNot VARCHAR2(200);
comment on column MDC_HypertensionVisit.medicineOtherNot is '其他不规律服药原因';
comment on column MDC_HypertensionVisit.medicineNot is '不规律服药原因';
alter table MDC_DiabetesVisit add medicineNot VARCHAR2(2);
alter table MDC_DiabetesVisit add medicineOtherNot VARCHAR2(200);
comment on column MDC_DiabetesVisit.medicineOtherNot is '其他不规律服药原因';
comment on column MDC_DiabetesVisit.medicineNot is '不规律服药原因';


  --2015-05-18--yaosq--------------
alter table MDC_DiabetesMedicine add medicineType VARCHAR2(2);
alter table MDC_DiabetesMedicine add medicineUseType VARCHAR2(2);
comment on column MDC_DiabetesMedicine.medicineType is '药物种类';
comment on column MDC_DiabetesMedicine.medicineUseType is '药物使用途径';
alter table MDC_HypertensionMedicine add medicineType VARCHAR2(2);
comment on column MDC_HypertensionMedicine.medicineType is '药物种类';
alter table MDC_Hypertension_FCBP add hypertensionHistory VARCHAR2(1);
alter table MDC_Hypertension_FCBP add hypertensionFirst VARCHAR2(1);
comment on column MDC_Hypertension_FCBP.hypertensionHistory is '高血压史';
comment on column MDC_Hypertension_FCBP.hypertensionFirst is '首次发现血压异常';



  --2015-05-19--zhoubn--------------
alter table MDC_DiabetesRecord add riskFactors varchar2(50);
comment on column MDC_DiabetesRecord.riskFactors is '危险因素';
alter table MDC_DiabetesVisit add fbsTest varchar2(50);
comment on column MDC_DiabetesVisit.fbsTest is '空腹测量方式';
alter table MDC_DiabetesVisit add pbsTest varchar2(50);
comment on column MDC_DiabetesVisit.pbsTest is '餐后测量方式';

  --2015-05-19--yaosq--------------
alter table MDC_DiabetesVisit add healthProposal varchar2(100);
comment on column MDC_DiabetesVisit.healthProposal is '健康处方建议';
alter table MDC_DiabetesVisit add otherHealthProposal varchar2(100);
comment on column MDC_DiabetesVisit.otherHealthProposal is '其他健康处方建议';
alter table MDC_HypertensionVisit add healthProposal varchar2(100);
comment on column MDC_HypertensionVisit.healthProposal is '健康处方建议';


  --2015-05-20--zhoubn--------------
create table MDC_DIABETESREPEATVISIT
(
  RECORDID    VARCHAR2(30) not null,
  VISITID      CHAR(20),
  EMPIID       CHAR(32),
  VISITDATE         DATE,
  DIAGNOSISDATE     DATE,
  DIAGNOSISUNIT     VARCHAR2(20),
  COMFIRMUNIT       VARCHAR2(20),
  FBS               NUMBER(8,2),
  PBS               NUMBER(8,2),
  CREATEUSER         VARCHAR2(20),
  CREATEDATE         TIMESTAMP(6),
  CREATEUNIT         VARCHAR2(20),
  LASTMODIFYUSER     VARCHAR2(20),
  LASTMODIFYDATE     TIMESTAMP(6),
  LASTMODIFYUNIT     VARCHAR2(20)
);
  comment on table MDC_DIABETESREPEATVISIT
  is '糖尿病复诊';
  comment on column MDC_DIABETESREPEATVISIT.RECORDID
  is '复诊主键';
  comment on column MDC_DIABETESREPEATVISIT.EMPIID
  is 'EMPIID';
  comment on column MDC_DIABETESREPEATVISIT.VISITID
  is '随访ID';
  comment on column MDC_DIABETESREPEATVISIT.VISITDATE
  is '随访日期';
  comment on column MDC_DIABETESREPEATVISIT.DIAGNOSISDATE
  is '诊断日期';
  comment on column MDC_DIABETESREPEATVISIT.DIAGNOSISUNIT
  is '诊断机构';
  comment on column MDC_DIABETESREPEATVISIT.COMFIRMUNIT
  is '确诊机构';
  comment on column MDC_DIABETESREPEATVISIT.FBS
  is '空腹血糖';
  comment on column MDC_DIABETESREPEATVISIT.PBS
  is '餐后血糖';
  
  comment on column MDC_DIABETESREPEATVISIT.CREATEUSER
    is '建档人';
  comment on column MDC_DIABETESREPEATVISIT.CREATEDATE
    is '建档日期';
  comment on column MDC_DIABETESREPEATVISIT.CREATEUNIT
    is '建档单位';
  comment on column MDC_DIABETESREPEATVISIT.LASTMODIFYUSER
    is '最后修改人';
  comment on column MDC_DIABETESREPEATVISIT.LASTMODIFYDATE
    is '最后修改日期';
  comment on column MDC_DIABETESREPEATVISIT.LASTMODIFYUNIT
    is '最后修改单位';
    
    
    
  --2015-05-21--zhoubn-------------- 
 create table SQ_ZKSFJH
(
  JLXH     	   VARCHAR2(30) not null,
  GRBM         VARCHAR2(30),
  EMPIID       CHAR(32),
  JHSFRQ       DATE,
  SJSFRQ       DATE,
  WCBZ         NUMBER(2,0),
  SFLB         NUMBER(2,0),
  ZBLB         NUMBER(2,0),
  KSRQ         DATE,
  JSRQ         DATE,
  TXRQ         DATE,
  YXBZ         NUMBER(2,0),
  CZGH         VARCHAR2(10),
  LRGH         VARCHAR2(10),
  CZJD         VARCHAR2(10),
  CZTD         VARCHAR2(10),
  GXJD         VARCHAR2(10),
  GXTD         VARCHAR2(10),
  JLBH         NUMBER(18,0),
  YCBZ         NUMBER(1,0),
  BZXX         VARCHAR2(400),
  GLSJ         DATE,
  INTIME       DATE,
  JCGF         NUMBER(1,0),
  UPD          NUMBER(1,0),
  ZB           NUMBER(8,0),
  KDYS         VARCHAR2(20),
  ZKYS         VARCHAR2(20),
  
  CREATEUSER         VARCHAR2(20),
  CREATEDATE         TIMESTAMP(6),
  CREATEUNIT         VARCHAR2(20),
  LASTMODIFYUSER     VARCHAR2(20),
  LASTMODIFYDATE     TIMESTAMP(6),
  LASTMODIFYUNIT     VARCHAR2(20)
);
  comment on table SQ_ZKSFJH
  is '质控随访计划表';
  comment on column SQ_ZKSFJH.JLXH
  is '记录主键';
  comment on column SQ_ZKSFJH.GRBM
  is 'GRBM';
  comment on column SQ_ZKSFJH.EMPIID
  is 'EMPIID';
  comment on column SQ_ZKSFJH.JHSFRQ
  is '计划随访日期';
  comment on column SQ_ZKSFJH.SJSFRQ
  is '实际随访日期';
  comment on column SQ_ZKSFJH.WCBZ
  is '完成标志';
  comment on column SQ_ZKSFJH.SFLB
  is '随访类别';
  comment on column SQ_ZKSFJH.ZBLB
  is '指标类别';
  comment on column SQ_ZKSFJH.KSRQ
  is '开始日期';
  comment on column SQ_ZKSFJH.JSRQ
  is '结束日期';
  comment on column SQ_ZKSFJH.TXRQ
  is '提醒日期';
  comment on column SQ_ZKSFJH.YXBZ
  is '有效标志';
  comment on column SQ_ZKSFJH.CZGH
  is '操作工号';
  comment on column SQ_ZKSFJH.LRGH
  is '录入工号';
  comment on column SQ_ZKSFJH.CZJD
  is '操作街道';
  comment on column SQ_ZKSFJH.CZTD
  is '操作团队';
  comment on column SQ_ZKSFJH.GXJD
  is '管辖街道';
  comment on column SQ_ZKSFJH.GXTD
  is '管辖团队';
  comment on column SQ_ZKSFJH.JLBH
  is '记录编号';
  comment on column SQ_ZKSFJH.YCBZ
  is '异常标志';
  comment on column SQ_ZKSFJH.BZXX
  is '备注信息';
  comment on column SQ_ZKSFJH.GLSJ
  is '管理时间';
  comment on column SQ_ZKSFJH.INTIME
  is '引入日期';
  comment on column SQ_ZKSFJH.JCGF
  is '检查规范';
  comment on column SQ_ZKSFJH.UPD
  is 'UPD';
  comment on column SQ_ZKSFJH.ZB
  is 'ZB';
  comment on column SQ_ZKSFJH.KDYS
  is '开单医生';
  comment on column SQ_ZKSFJH.ZKYS
  is '质控医生';
  
  comment on column SQ_ZKSFJH.CREATEUSER
    is '建档人';
  comment on column SQ_ZKSFJH.CREATEDATE
    is '建档日期';
  comment on column SQ_ZKSFJH.CREATEUNIT
    is '建档单位';
  comment on column SQ_ZKSFJH.LASTMODIFYUSER
    is '最后修改人';
  comment on column SQ_ZKSFJH.LASTMODIFYDATE
    is '最后修改日期';
  comment on column SQ_ZKSFJH.LASTMODIFYUNIT
    is '最后修改单位';
    


create table SQ_JY01
(
  ID           VARCHAR2(50) not null,
  JLXH         VARCHAR2(30) not null,
  YCBZ         NUMBER(2,0),
  JKKH         VARCHAR2(20),
  GRBM         VARCHAR2(30),
  EMPIID       CHAR(32),
  JYDH         VARCHAR2(64),
  JYRQ         DATE,
  JYFF         VARCHAR2(20),
  KDRQ         DATE,
  KDKS         VARCHAR2(20),
  KDYS         VARCHAR2(20),
  JYKS         VARCHAR2(20),
  JYYS         VARCHAR2(20),
  FHYS         VARCHAR2(20),
  YYMC         VARCHAR2(100),
  SFRQ         DATE,
  INTIME       DATE,
  CZGH         VARCHAR2(20),
  YRBZ         NUMBER(1,0),
  UPD          NUMBER(1,0),

  CREATEUSER         VARCHAR2(20),
  CREATEDATE         TIMESTAMP(6),
  CREATEUNIT         VARCHAR2(20),
  LASTMODIFYUSER     VARCHAR2(20),
  LASTMODIFYDATE     TIMESTAMP(6),
  LASTMODIFYUNIT     VARCHAR2(20)
);
  comment on table SQ_JY01
  is '检验表';
  comment on column SQ_JY01.ID
  is '检验单ID';
  comment on column SQ_JY01.JLXH
  is '记录序号';
  comment on column SQ_JY01.YCBZ
  is '异常标志';
  comment on column SQ_JY01.JKKH
  is '健康卡号';
  comment on column SQ_JY01.GRBM
  is '个人编码';
  comment on column SQ_JY01.EMPIID
  is 'EMPIID';
  comment on column SQ_JY01.JYDH
  is '检验单号';
  comment on column SQ_JY01.JYRQ
  is '检验日期';
  comment on column SQ_JY01.JYFF
  is '检验方法';
  comment on column SQ_JY01.KDRQ
  is '开单日期';
  comment on column SQ_JY01.KDKS
  is '开单科室';
  comment on column SQ_JY01.KDYS
  is '开单医生';
  comment on column SQ_JY01.JYKS
  is '检验科室';
  comment on column SQ_JY01.JYYS
  is '检验医生';
  comment on column SQ_JY01.FHYS
  is '复核医生';
  comment on column SQ_JY01.YYMC
  is '医院名称';
  comment on column SQ_JY01.SFRQ
  is '随访日期';
  comment on column SQ_JY01.INTIME
  is '引入日期';
  comment on column SQ_JY01.CZGH
  is '操作工号';
  comment on column SQ_JY01.YRBZ
  is 'YRBZ';
  comment on column SQ_JY01.UPD
  is 'UPD';
  
  comment on column SQ_JY01.CREATEUSER
    is '建档人';
  comment on column SQ_JY01.CREATEDATE
    is '建档日期';
  comment on column SQ_JY01.CREATEUNIT
    is '建档单位';
  comment on column SQ_JY01.LASTMODIFYUSER
    is '最后修改人';
  comment on column SQ_JY01.LASTMODIFYDATE
    is '最后修改日期';
  comment on column SQ_JY01.LASTMODIFYUNIT
    is '最后修改单位';
    
    
    
 create table SQ_JY02
(
  JLXH         VARCHAR2(18) not null,
  JYDH         VARCHAR2(64),
  JYRQ         DATE,
  XMBH         VARCHAR2(20),
  XMMC         VARCHAR2(40),
  XMZ          VARCHAR2(20),
  DW           VARCHAR2(32),
  CKFW         VARCHAR2(32),
  JYZB         VARCHAR2(10),
  HBBM         VARCHAR2(20),
  BZ           VARCHAR2(20),
  INTIME       DATE,
  ZBLB         NUMBER,

  CREATEUSER         VARCHAR2(20),
  CREATEDATE         TIMESTAMP(6),
  CREATEUNIT         VARCHAR2(20),
  LASTMODIFYUSER     VARCHAR2(20),
  LASTMODIFYDATE     TIMESTAMP(6),
  LASTMODIFYUNIT     VARCHAR2(20)
);
  comment on table SQ_JY02
  is '检验明细表';
  comment on column SQ_JY02.JLXH
  is '记录序号';
  comment on column SQ_JY02.JYDH
  is '检验单号';
  comment on column SQ_JY02.JYRQ
  is '检验日期';
  comment on column SQ_JY02.XMBH
  is '项目编号';
  comment on column SQ_JY02.XMMC
  is '项目名称';
  comment on column SQ_JY02.XMZ
  is '项目值';
  comment on column SQ_JY02.DW
  is '单位';
  comment on column SQ_JY02.CKFW
  is '参考范围';
  comment on column SQ_JY02.JYZB
  is '检验指标';
  comment on column SQ_JY02.HBBM
  is '黄本编码';
  comment on column SQ_JY02.BZ
  is '备注';
  comment on column SQ_JY02.INTIME
  is '引入时间';
  comment on column SQ_JY02.ZBLB
  is '指标类别';
  
  comment on column SQ_JY02.CREATEUSER
    is '建档人';
  comment on column SQ_JY02.CREATEDATE
    is '建档日期';
  comment on column SQ_JY02.CREATEUNIT
    is '建档单位';
  comment on column SQ_JY02.LASTMODIFYUSER
    is '最后修改人';
  comment on column SQ_JY02.LASTMODIFYDATE
    is '最后修改日期';
  comment on column SQ_JY02.LASTMODIFYUNIT
    is '最后修改单位';
    
    
 
create table SQ_MUSE
(
  JLXH         VARCHAR2(18) not null,
  YCBZ         NUMBER(2,0),
  JKKH         VARCHAR2(20),
  KDRQ         DATE,
  GRBM         VARCHAR2(30),
  EMPIID       CHAR(32),
  JCXM         VARCHAR2(40),
  KDKS         VARCHAR2(20),
  KDYS         VARCHAR2(20),
  JCYY         VARCHAR2(40),
  JCLX         NUMBER(2,0),
  KDKH         VARCHAR2(32),
  KDDH         VARCHAR2(32),
  SFRQ         DATE,
  BGMS         VARCHAR2(200),
  BZ           VARCHAR2(100),
  INTIME       DATE,
  CZGH         VARCHAR2(20),
  YRBZ         NUMBER(2,0),

  CREATEUSER         VARCHAR2(20),
  CREATEDATE         TIMESTAMP(6),
  CREATEUNIT         VARCHAR2(20),
  LASTMODIFYUSER     VARCHAR2(20),
  LASTMODIFYDATE     TIMESTAMP(6),
  LASTMODIFYUNIT     VARCHAR2(20)
);
  comment on table SQ_MUSE
  is '心电图表';
  comment on column SQ_MUSE.JLXH
  is '记录序号';
  comment on column SQ_MUSE.YCBZ
  is '异常标志';
  comment on column SQ_MUSE.JKKH
  is '健康卡号';
  comment on column SQ_MUSE.KDRQ
  is '开单日期';
  comment on column SQ_MUSE.GRBM
  is '个人编码';
  comment on column SQ_MUSE.EMPIID
  is 'EMPIID';
  comment on column SQ_MUSE.JCXM
  is '检查项目';
  comment on column SQ_MUSE.KDKS
  is '开单科室';
  comment on column SQ_MUSE.KDYS
  is '开单医生';
  comment on column SQ_MUSE.JCYY
  is '检查医院';
  comment on column SQ_MUSE.JCLX
  is '检查类别';
  comment on column SQ_MUSE.KDKH
  is '开单卡号';
  comment on column SQ_MUSE.KDDH
  is '开单单号';
  comment on column SQ_MUSE.SFRQ
  is '随访日期';
  comment on column SQ_MUSE.BGMS
  is '报告描述';
    comment on column SQ_MUSE.BZ
  is '备注';
  comment on column SQ_MUSE.INTIME
  is '引入时间';
  comment on column SQ_MUSE.CZGH
  is '操作工号';
  comment on column SQ_MUSE.YRBZ
  is '异常标志';
  
  comment on column SQ_MUSE.CREATEUSER
    is '建档人';
  comment on column SQ_MUSE.CREATEDATE
    is '建档日期';
  comment on column SQ_MUSE.CREATEUNIT
    is '建档单位';
  comment on column SQ_MUSE.LASTMODIFYUSER
    is '最后修改人';
  comment on column SQ_MUSE.LASTMODIFYDATE
    is '最后修改日期';
  comment on column SQ_MUSE.LASTMODIFYUNIT
    is '最后修改单位'; 
    
 

create table SQ_PACS
(
  JLXH         VARCHAR2(18) not null,
  YCBZ         NUMBER(2,0),
  JKKH         VARCHAR2(20),
  GRBM         VARCHAR2(30),
  EMPIID       CHAR(32),
  JCH          VARCHAR2(40),
  JCYY         VARCHAR2(40),
  ZDYY         VARCHAR2(40),
  HZLX         NUMBER(2,0),
  YXH          VARCHAR2(40),
  SQDH         VARCHAR2(40),
  SBLX         VARCHAR2(40),
  SBMX         VARCHAR2(40),
  JCBWFF       VARCHAR2(100),
  JCSJ         DATE,
  JCZT         VARCHAR2(20),
  SFYX         VARCHAR2(10),
  BGMS         VARCHAR2(4000),
  BGZD         VARCHAR2(2000),
  BGJY         VARCHAR2(1000),
  BGYS         VARCHAR2(40),
  BGSJ         DATE,
  SFRQ         DATE,
  INTIME       DATE,
  CZGH         VARCHAR2(20),
  YRBZ         NUMBER(1,0),

  CREATEUSER         VARCHAR2(20),
  CREATEDATE         TIMESTAMP(6),
  CREATEUNIT         VARCHAR2(20),
  LASTMODIFYUSER     VARCHAR2(20),
  LASTMODIFYDATE     TIMESTAMP(6),
  LASTMODIFYUNIT     VARCHAR2(20)
);
  comment on table SQ_PACS
  is '胸片检查表';
  comment on column SQ_PACS.JLXH
  is '记录序号';
  comment on column SQ_PACS.YCBZ
  is '异常标志';
  comment on column SQ_PACS.JKKH
  is '健康卡号';
  comment on column SQ_PACS.GRBM
  is '个人编码';
  comment on column SQ_PACS.EMPIID
  is 'EMPIID';
  comment on column SQ_PACS.JCH
  is '检查号';
  comment on column SQ_PACS.JCYY
  is '检查医院';
  comment on column SQ_PACS.ZDYY
  is '诊断医院';
  comment on column SQ_PACS.HZLX
  is '患者类型';
  comment on column SQ_PACS.YXH
  is '影像号';
  comment on column SQ_PACS.SQDH
  is '申请单号';
  comment on column SQ_PACS.SBLX
  is '设备类型';
  comment on column SQ_PACS.SBMX
  is '设备明细';
  comment on column SQ_PACS.JCBWFF
  is '检查部位方式';
  comment on column SQ_PACS.JCSJ
  is '检查时间';
  comment on column SQ_PACS.JCZT
  is '检查状态';
  comment on column SQ_PACS.SFYX
  is '随访有效';
  comment on column SQ_PACS.BGMS
  is '报告描述';
  comment on column SQ_PACS.BGZD
  is '报告诊断';
  comment on column SQ_PACS.BGJY
  is '报告校验';
 
  comment on column SQ_PACS.BGYS
  is '报告医生';
  comment on column SQ_PACS.BGSJ
  is '报告时间';
  comment on column SQ_PACS.SFRQ
  is '随访日期';
  comment on column SQ_PACS.INTIME
  is '引入时间';
  comment on column SQ_PACS.CZGH
  is '操作工号';   
  comment on column SQ_PACS.YRBZ
  is '异常标志';    

  comment on column SQ_PACS.CREATEUSER
    is '建档人';
  comment on column SQ_PACS.CREATEDATE
    is '建档日期';
  comment on column SQ_PACS.CREATEUNIT
    is '建档单位';
  comment on column SQ_PACS.LASTMODIFYUSER
    is '最后修改人';
  comment on column SQ_PACS.LASTMODIFYDATE
    is '最后修改日期';
  comment on column SQ_PACS.LASTMODIFYUNIT
    is '最后修改单位';

--2015-05-28--zhoubn--------------------
-- Create table
create table CVD_DiseaseManagement
(
  RECORDID       CHAR(16) not null,
  EMPIID         VARCHAR2(32) not null,
  MZHM           VARCHAR2(32),
  ZYHM           VARCHAR2(32),
  JZJG           VARCHAR2(32),
  JBLX           NUMBER(2),
  BGLX           NUMBER(2),
  FBCX           VARCHAR2(32),
  SCFBRQ         DATE,
  BCFBRQSJ       DATE,
  SWRQSJ         DATE,
  JZRQSJ         DATE,
  QZRQSJ         DATE,
  SSY            NUMBER(8),
  SZY            NUMBER(8),
  ICD            VARCHAR2(20),
  ICDNAME        VARCHAR2(80),
  JBFL           NUMBER(2),
  TIA            NUMBER(1),
  ZDYJ           VARCHAR2(50),
  BZ             VARCHAR2(100),
  XXLY           NUMBER(2),
  BGJG           VARCHAR2(32),
  BGYS           VARCHAR2(20),
  BGRQ           DATE,
  LBBB           NUMBER(1),
  LBYY           NUMBER(2),
  LBJG           VARCHAR2(32),
  CREATEUSER     VARCHAR2(20),
  CREATEUNIT     VARCHAR2(32),
  CREATEDATE     DATE,
  LASTMODIFYUSER VARCHAR2(20),
  LASTMODIFYUNIT VARCHAR2(32),
  LASTMODIFYDATE DATE,
  HSZT           NUMBER(1)
);
-- Add comments to the table 
comment on table CVD_DiseaseManagement
  is '心脑血管疾病管理';
-- Add comments to the columns 
comment on column CVD_DiseaseManagement.RECORDID
  is '记录号';
comment on column CVD_DiseaseManagement.EMPIID
  is 'EMPIID';
comment on column CVD_DiseaseManagement.MZHM
  is '门诊号码';
comment on column CVD_DiseaseManagement.ZYHM
  is '住院号码';
comment on column CVD_DiseaseManagement.JZJG
  is '就诊机构';
comment on column CVD_DiseaseManagement.JBLX
  is '疾病类型';
comment on column CVD_DiseaseManagement.BGLX
  is '报告类型';
comment on column CVD_DiseaseManagement.FBCX
  is '发病次序';
comment on column CVD_DiseaseManagement.SCFBRQ
  is '首次发病日期(年月日)';
comment on column CVD_DiseaseManagement.BCFBRQSJ
  is '本次发病日期时间(年月日时分秒)';
comment on column CVD_DiseaseManagement.SWRQSJ
  is '死亡日期时间(年月日时分秒)';
comment on column CVD_DiseaseManagement.JZRQSJ
  is '就诊日期时间(年月日时分秒)';
comment on column CVD_DiseaseManagement.QZRQSJ
  is '确诊日期时间(年月日时分秒)';
comment on column CVD_DiseaseManagement.SSY
  is '收缩压';
comment on column CVD_DiseaseManagement.SZY
  is '舒张压';
comment on column CVD_DiseaseManagement.ICD
  is 'ICD10编码';
comment on column CVD_DiseaseManagement.ICDNAME
  is 'ICD10编码对应名称';
comment on column CVD_DiseaseManagement.JBFL
  is '疾病分类';
comment on column CVD_DiseaseManagement.TIA
  is 'TIA发作(是1否0)';
comment on column CVD_DiseaseManagement.ZDYJ
  is '诊断依据';
comment on column CVD_DiseaseManagement.BZ
  is '备注';
comment on column CVD_DiseaseManagement.XXLY
  is '信息来源';
comment on column CVD_DiseaseManagement.BGJG
  is '报告机构';
comment on column CVD_DiseaseManagement.BGYS
  is '报告医师';
comment on column CVD_DiseaseManagement.BGRQ
  is '报告日期';
comment on column CVD_DiseaseManagement.LBBB
  is '漏报补报(是1否0)';
comment on column CVD_DiseaseManagement.LBYY
  is '漏报原因';
comment on column CVD_DiseaseManagement.LBJG
  is '漏报机构(医院)';
comment on column CVD_DiseaseManagement.CREATEUSER
  is '录入人';
comment on column CVD_DiseaseManagement.CREATEUNIT
  is '录入机构';
comment on column CVD_DiseaseManagement.CREATEDATE
  is '录入时间';
comment on column CVD_DiseaseManagement.LASTMODIFYUSER
  is '最后修改人';
comment on column CVD_DiseaseManagement.LASTMODIFYUNIT
  is '最后修改机构';
comment on column CVD_DiseaseManagement.LASTMODIFYDATE
  is '最后修改时间';
comment on column CVD_DiseaseManagement.HSZT
  is '核实状态(已核实1未核实0)';


-- Create table
create table CVD_DiseaseOmission
(
  RECORDID       CHAR(16) not null,
  EMPIID         VARCHAR2(32) not null,
  MZHM           VARCHAR2(32),
  JZJG           VARCHAR2(32),
  JZSJ           DATE,                    
  JBZD           VARCHAR2(32),
  MZYS           VARCHAR2(32),
  SFBK           NUMBER(1),
  CREATEUSER     VARCHAR2(20),
  CREATEUNIT     VARCHAR2(32),
  CREATEDATE     DATE,
  LASTMODIFYUSER VARCHAR2(20),
  LASTMODIFYUNIT VARCHAR2(32),
  LASTMODIFYDATE DATE
);
-- Add comments to the table 
comment on table CVD_DiseaseOmission
  is '心脑血管疾病漏报';
-- Add comments to the columns 
comment on column CVD_DiseaseOmission.RECORDID
  is '记录号';
comment on column CVD_DiseaseOmission.EMPIID
  is 'EMPIID';
comment on column CVD_DiseaseOmission.MZHM
  is '门诊号码';
comment on column CVD_DiseaseOmission.JZJG
  is '就诊机构';
comment on column CVD_DiseaseOmission.JZSJ
  is '就诊时间';
comment on column CVD_DiseaseOmission.JBZD
  is '疾病诊断';
comment on column CVD_DiseaseOmission.MZYS
  is '门诊医生';
comment on column CVD_DiseaseOmission.SFBK
  is '是否报卡';
comment on column CVD_DiseaseOmission.CREATEUSER
  is '录入人';
comment on column CVD_DiseaseOmission.CREATEUNIT
  is '录入机构';
comment on column CVD_DiseaseOmission.CREATEDATE
  is '录入时间';
comment on column CVD_DiseaseOmission.LASTMODIFYUSER
  is '最后修改人';
comment on column CVD_DiseaseOmission.LASTMODIFYUNIT
  is '最后修改机构';
comment on column CVD_DiseaseOmission.LASTMODIFYDATE
  is '最后修改时间';

-- Create table
create table CVD_DISEASEVERIFICATION
(
  RECORDID       CHAR(16) not null,
  PRECORDID      CHAR(16) not null,
  EMPIID         VARCHAR2(32),
  DCYS           VARCHAR2(20),
  DCJG           VARCHAR2(32),
  SF             VARCHAR2(1),
  FBDCRQ         DATE,
  SWDCRQ         DATE,
  FWSBYY         VARCHAR2(1),
  SFYYOTHER      VARCHAR2(100),
  NGS            VARCHAR2(1),
  NCX            VARCHAR2(1),
  ZZBFX          VARCHAR2(1),
  QXXNGS         VARCHAR2(1),
  ZWMXQCX        VARCHAR2(1),
  XJGS           VARCHAR2(1),
  SWDD           VARCHAR2(2),
  SWDDOTHER      VARCHAR2(100),
  ZJSY           VARCHAR2(30),
  ZJSYOTHER      VARCHAR2(32),
  CCFBHSWYY      VARCHAR2(30), 
  CCFBHSWYYOTHER VARCHAR2(32),   
  FZHCHSJ        DATE,
  ZLLY           VARCHAR2(1),  
  ZLLYOTHER      VARCHAR2(32),  
  GXY            VARCHAR2(1),    
  TNB            VARCHAR2(1),   
  GZXZ           VARCHAR2(1),    
  TIA            VARCHAR2(1),   
  GXB            VARCHAR2(1),   
  FC             VARCHAR2(1),   
  JDMBKHXZ       VARCHAR2(1),    
  XZBOTHER       VARCHAR2(32),  
  SFCZXY         VARCHAR2(1), 
   
  MTKSXYNL       VARCHAR2(3),  
  RJXYZS         VARCHAR2(3), 
  FPCZ           VARCHAR2(1), 
  BMI            VARCHAR2(32), 
  QFYD           VARCHAR2(1),
  YJS            VARCHAR2(1),
  RYJL           VARCHAR2(32), 
  YJZL           VARCHAR2(1),
  GLYJ           VARCHAR2(1), 
  ZZJZS          VARCHAR2(1), 
  GXYJZS         VARCHAR2(1),
  GXBJZS         VARCHAR2(1),
  WXYSOTHER      VARCHAR2(32),

  CREATEUSER     VARCHAR2(20),
  CREATEUNIT     VARCHAR2(32),
  CREATEDATE     DATE,
  LASTMODIFYUSER VARCHAR2(20),
  LASTMODIFYUNIT VARCHAR2(32),
  LASTMODIFYDATE DATE

);

-- Add comments to the table 
comment on table CVD_DISEASEVERIFICATION
  is '心脑血管疾病初访核实';
-- Add comments to the columns 
comment on column CVD_DISEASEVERIFICATION.RECORDID
  is '记录号';
comment on column CVD_DISEASEVERIFICATION.PRECORDID
  is '父记录号';
comment on column CVD_DISEASEVERIFICATION.EMPIID
  is 'EMPIID';
comment on column CVD_DISEASEVERIFICATION.DCYS
  is '调查医师';
comment on column CVD_DISEASEVERIFICATION.DCJG
  is '调查机构';
comment on column CVD_DISEASEVERIFICATION.SF
  is '失访';
comment on column CVD_DISEASEVERIFICATION.FBDCRQ
  is '发病调查日期';
comment on column CVD_DISEASEVERIFICATION.SWDCRQ
  is '死亡调查日期';
comment on column CVD_DISEASEVERIFICATION.FWSBYY
  is '访问失败原因';
comment on column CVD_DISEASEVERIFICATION.SFYYOTHER
  is '其他失访原因';
comment on column CVD_DISEASEVERIFICATION.NGS
  is '脑梗死';
comment on column CVD_DISEASEVERIFICATION.NCX
  is '脑出血';
comment on column CVD_DISEASEVERIFICATION.ZZBFX
  is '卒中不分型';
comment on column CVD_DISEASEVERIFICATION.QXXNGS
  is '腔隙性脑梗死';
comment on column CVD_DISEASEVERIFICATION.ZWMXQCX
  is '蛛网膜下腔出血';
comment on column CVD_DISEASEVERIFICATION.XJGS
  is '心机梗死';
comment on column CVD_DISEASEVERIFICATION.SWDD
  is '死亡地点';
comment on column CVD_DISEASEVERIFICATION.SWDDOTHER
  is '其他地点';
comment on column CVD_DISEASEVERIFICATION.ZJSY
  is '直接死因';
comment on column CVD_DISEASEVERIFICATION.ZJSYOTHER
  is '其他死因';
comment on column CVD_DISEASEVERIFICATION.CCFBHSWYY
  is '此次发病或死亡的诱因';
comment on column CVD_DISEASEVERIFICATION.CCFBHSWYYOTHER
  is '其他诱因';
comment on column CVD_DISEASEVERIFICATION.FZHCHSJ
  is '发作后存活时间';
comment on column CVD_DISEASEVERIFICATION.ZLLY
  is '资料来源';
comment on column CVD_DISEASEVERIFICATION.ZLLYOTHER
  is '资料来源其他';
comment on column CVD_DISEASEVERIFICATION.GXY
  is '高血压';
comment on column CVD_DISEASEVERIFICATION.TNB
  is '糖尿病';
comment on column CVD_DISEASEVERIFICATION.GZXZ
  is '高脂血症';
comment on column CVD_DISEASEVERIFICATION.TIA
  is 'TIA';
comment on column CVD_DISEASEVERIFICATION.GXB
  is '冠心病';
comment on column CVD_DISEASEVERIFICATION.FC
  is '房颤';
comment on column CVD_DISEASEVERIFICATION.JDMBKHXZ
  is '颈动脉斑块或狭窄';
comment on column CVD_DISEASEVERIFICATION.XZBOTHER
  is '其他心脏病';
comment on column CVD_DISEASEVERIFICATION.SFCZXY
  is '是否存在吸烟';
  
comment on column CVD_DISEASEVERIFICATION.MTKSXYNL
  is '每天开始吸烟的年龄';  
comment on column CVD_DISEASEVERIFICATION.RJXYZS
  is '日均吸烟支数'; 
comment on column CVD_DISEASEVERIFICATION.FPCZ
  is '肥胖或超重(BMI≥24)'; 
comment on column CVD_DISEASEVERIFICATION.BMI
  is 'BMI'; 
comment on column CVD_DISEASEVERIFICATION.QFYD
  is '缺乏运动'; 
comment on column CVD_DISEASEVERIFICATION.YJS
  is '饮酒史'; 
comment on column CVD_DISEASEVERIFICATION.RYJL
  is '日饮酒量（相当于白酒）'; 
comment on column CVD_DISEASEVERIFICATION.YJZL
  is '饮酒种类'; 
comment on column CVD_DISEASEVERIFICATION.GLYJ
  is '过量饮酒'; 
comment on column CVD_DISEASEVERIFICATION.ZZJZS
  is '卒中家族史'; 
comment on column CVD_DISEASEVERIFICATION.GXYJZS
  is '高血压家族史'; 
comment on column CVD_DISEASEVERIFICATION.GXBJZS
  is '冠心病家族史'; 
comment on column CVD_DISEASEVERIFICATION.WXYSOTHER
  is '其他行为危险因素'; 
  
comment on column CVD_DISEASEVERIFICATION.CREATEUSER
  is '录入人';  
comment on column CVD_DISEASEVERIFICATION.CREATEUNIT
  is '录入机构';
comment on column CVD_DISEASEVERIFICATION.CREATEDATE
  is '录入时间';
comment on column CVD_DISEASEVERIFICATION.LASTMODIFYUSER
  is '最后修改人';
comment on column CVD_DISEASEVERIFICATION.LASTMODIFYUNIT
  is '最后修改机构';
comment on column CVD_DISEASEVERIFICATION.LASTMODIFYDATE
  is '最后修改时间';    
    
    
      --2015-05-22--yaosq--------------    
alter table mdc_hypertensionrecord add endCheck VARCHAR2(1);
alter table mdc_hypertensionrecord add visitEffect VARCHAR2(1);
alter table mdc_hypertensionrecord add noVisitReason VARCHAR2(1);
alter table mdc_hypertensionrecord add visitDate DATE;
alter table mdc_hypertensionrecord add deadDate DATE;
alter table mdc_diabetesrecord add endCheck VARCHAR2(1);
alter table mdc_diabetesrecord add visitEffect VARCHAR2(1);
alter table mdc_diabetesrecord add noVisitReason VARCHAR2(1);
alter table mdc_diabetesrecord add visitDate DATE;
alter table mdc_diabetesrecord add deadDate DATE;
comment on column mdc_hypertensionrecord.endCheck
    is '终止核实情况';
comment on column mdc_hypertensionrecord.visitEffect
    is '转归';
comment on column mdc_hypertensionrecord.noVisitReason
    is '终止原因';
comment on column mdc_hypertensionrecord.visitDate
    is '终止日期';
comment on column mdc_diabetesrecord.endCheck
    is '终止核实情况';
comment on column mdc_diabetesrecord.visitEffect
    is '转归';
comment on column mdc_diabetesrecord.noVisitReason
    is '终止原因';
comment on column mdc_diabetesrecord.visitDate
    is '终止日期';
comment on column mdc_hypertensionrecord.deadDate
    is '死亡日期';
comment on column mdc_diabetesrecord.deadDate
    is '死亡日期';
    --chenxr----2015-05-22-------------------
alter table IDR_REPORT add ms_brzd_jlbh VARCHAR2(20);
comment on column IDR_REPORT.ms_brzd_jlbh
  is '门诊诊断记录编号';    
   --2015-05-29--yaosq--------------  
alter table MDC_HypertensionFixGroup modify fixId VARCHAR2(16);

alter table MDC_HypertensionRecord add drinkOver VARCHAR2(1);
comment on column MDC_HypertensionRecord.drinkOver is '饮酒过量';    
alter table MDC_HypertensionRecord add familyHistroyOther VARCHAR2(100);
comment on column MDC_HypertensionRecord.familyHistroyOther is '家族史其他'; 

   --2015-06-3--yaosq--------------  
alter table MDC_DiabetesVisit add diabetesType CHAR(1);
alter table MDC_DiabetesVisit add diabetesChange CHAR(1);

--2015-6-3 fangy--------------------
ALTER TABLE MDC_TUMOURSCREENING ADD ( CHECKITEM4 VARCHAR2(100) NULL);
COMMENT ON COLUMN MDC_TUMOURSCREENING.CHECKITEM4 IS '检查项目4';
ALTER TABLE MDC_TUMOURSCREENING ADD ( CHECKRESULT4 VARCHAR2(30) NULL);
COMMENT ON COLUMN MDC_TUMOURSCREENING.CHECKRESULT4 IS '检查结果4';

alter table MDC_HypertensionRecord add SFNRXJGL VARCHAR2(2);
comment on column MDC_HypertensionRecord.SFNRXJGL is '是否纳入细节管理'; 
alter table MDC_HypertensionRecord add XJGLDXBH VARCHAR2(32);
comment on column MDC_HypertensionRecord.XJGLDXBH is '细节管理序号';


--fy--------add--------2015-12-16---------------------------
/*==============================================================*/
/* Table: TB_TuberculosisFirstVisit 肺结核患者第一次入户随访    */
/*==============================================================*/
create table TB_TuberculosisFirstVisit  (
   visitId              varchar2(16)                    not null,
   empiId               varchar2(32),
   RecordId             varchar2(30),
   visitDate            date,
   visitType            varchar2(1),
   patientType          varchar2(1),
   sputumSatus          varchar2(1),
   resistanceStatus     varchar2(1),
   symptomSign          varchar2(50),
   otherSymptomSign     varchar2(50),
   chemotherapy         varchar2(50),
   medicineUsage        varchar2(1),
   medicineType         varchar2(10),
   supervisor           varchar2(1),
   singleRoom           varchar2(1),
   smokePerDay          varchar2(10),
   smokeDay             varchar2(10),
   drinkPerDay          varchar2(10),
   drinkDay             varchar2(10),
   takeLocation         varchar2(50),
   takeTime             date,
   medicineRecordFill   varchar2(1),
   medicineStore        varchar2(32),
   treatmentCourse      varchar2(1),
   noRegularMedicineRisk varchar2(1),
   medicineBadReactionTreatment varchar2(1),
   treatmentReferralSputum varchar2(1),
   persistMedicineDuringOut varchar2(1),
   lifestylePrecautions varchar2(1),
   closeContactCheck    varchar2(1),
   nextDate             date,
   visitDoctor          varchar2(20),
   createUser           varchar2(20),
   createUnit           varchar2(20),
   createDate           date,
   lastModifyUnit       varchar2(20),
   lastModifyUser       varchar2(20),
   lastModifyDate       DATE,
   constraint PK_TB_TUBERCULOSISFIRSTVISIT primary key (visitId)
);
 comment on table TB_TuberculosisFirstVisit is
'肺结核患者第一次入户随访';
 comment on column TB_TuberculosisFirstVisit.visitId is
'随访ID';
 comment on column TB_TuberculosisFirstVisit.empiId is
'empiId';
 comment on column TB_TuberculosisFirstVisit.RecordId is
'档案编号';
 comment on column TB_TuberculosisFirstVisit.visitDate is
'随访时间';
 comment on column TB_TuberculosisFirstVisit.visitType is
'随访方式';
 comment on column TB_TuberculosisFirstVisit.patientType is
'患者类型';
 comment on column TB_TuberculosisFirstVisit.sputumSatus is
'痰菌情况';
 comment on column TB_TuberculosisFirstVisit.resistanceStatus is
'耐药情况';
 comment on column TB_TuberculosisFirstVisit.symptomSign is
'症状及体征';
 comment on column TB_TuberculosisFirstVisit.otherSymptomSign is
'其他症状及体征';
 comment on column TB_TuberculosisFirstVisit.chemotherapy is
'化疗方案';
 comment on column TB_TuberculosisFirstVisit.medicineUsage is
'药物用法';
 comment on column TB_TuberculosisFirstVisit.medicineType is
'药品剂型';
 comment on column TB_TuberculosisFirstVisit.supervisor is
'督导人员';
 comment on column TB_TuberculosisFirstVisit.singleRoom is
'单独的居室';
 comment on column TB_TuberculosisFirstVisit.smokePerDay is
'吸烟每天';
 comment on column TB_TuberculosisFirstVisit.smokeDay is
'吸烟天';
 comment on column TB_TuberculosisFirstVisit.drinkPerDay is
'饮酒每天';
 comment on column TB_TuberculosisFirstVisit.drinkDay is
'饮酒天';
 comment on column TB_TuberculosisFirstVisit.takeLocation is
'取药地点';
 comment on column TB_TuberculosisFirstVisit.takeTime is
'取药时间';
 comment on column TB_TuberculosisFirstVisit.medicineRecordFill is
'服药记录卡的填写';
 comment on column TB_TuberculosisFirstVisit.medicineStore is
'服药方法及药品存放';
 comment on column TB_TuberculosisFirstVisit.treatmentCourse is
'肺结核治疗疗程';
 comment on column TB_TuberculosisFirstVisit.noRegularMedicineRisk is
'不规律服药危害';
 comment on column TB_TuberculosisFirstVisit.medicineBadReactionTreatment is
'服药后不良反应及处理';
 comment on column TB_TuberculosisFirstVisit.treatmentReferralSputum is
'治疗期间复诊查痰';
 comment on column TB_TuberculosisFirstVisit.persistMedicineDuringOut is
'外出期间如何坚持服药';
 comment on column TB_TuberculosisFirstVisit.lifestylePrecautions is
'生活习惯及注意事项';
 comment on column TB_TuberculosisFirstVisit.closeContactCheck is
'密切接触者检查';
 comment on column TB_TuberculosisFirstVisit.nextDate is
'下次随访时间';
 comment on column TB_TuberculosisFirstVisit.visitDoctor is
'评估医生签名';
 comment on column TB_TuberculosisFirstVisit.createUser is
'createUser';
 comment on column TB_TuberculosisFirstVisit.createUnit is
'createUnit';
 comment on column TB_TuberculosisFirstVisit.createDate is
'createDate';
 comment on column TB_TuberculosisFirstVisit.lastModifyUnit is
'lastModifyUnit';
 comment on column TB_TuberculosisFirstVisit.lastModifyUser is
'lastModifyUser';
 comment on column TB_TuberculosisFirstVisit.lastModifyDate is
'lastModifyDate';

/*==============================================================*/
/* Table: TB_TuberculosisVisit         肺结核患者随访           */
/*==============================================================*/
create table TB_TuberculosisVisit  (
   visitId              varchar2(16)                    not null,
   empiId               varchar2(32),
   RecordId             varchar2(30),
   visitDate            date,
   treatmentMonthOrder  varchar2(10),
   supervisor           varchar2(1),
   visitType            varchar2(1),
   symptomSign          varchar2(50),
   otherSymptomSign     varchar2(50),
   smokePerDay          varchar2(10),
   smokeDay             varchar2(10),
   drinkPerDay          varchar2(10),
   drinkDay             varchar2(10),
   chemotherapy         varchar2(50),
   medicineUsage        varchar2(1),
   medicineType         varchar2(10),
   missMedicineNum      varchar2(10),
   medicineBadReaction  varchar2(1),
   medicineBadReactionDesc varchar2(100),
   complicationComorbidity varchar2(1),
   complicationComorbidityDesc varchar2(100),
   referralDivision     varchar2(50),
   referralReason       varchar2(100),
   twoWeekVisitResult   varchar2(100),
   processAdvice        varchar2(100),
   nextDate             date,
   visitDoctor          varchar2(20),
   stopTreatmentTime    date,
   stopTreatmentReason  varchar2(20),
   shouldVisitNum       varchar2(10),
   actualVisitNum       varchar2(10),
   shouldMedicineNum    varchar2(10),
   actualMedicineNum    varchar2(10),
   assessDoctor         varchar2(20),
   createUser           varchar2(20),
   createUnit           varchar2(20),
   createDate           date,
   lastModifyUnit       varchar2(20),
   lastModifyUser       varchar2(20),
   lastModifyDate       DATE,
   constraint PK_TB_TUBERCULOSISVISIT primary key (visitId)
);
 comment on table TB_TuberculosisVisit is
'肺结核患者随访';
 comment on column TB_TuberculosisVisit.visitId is
'随访ID';
 comment on column TB_TuberculosisVisit.empiId is
'empiId';
 comment on column TB_TuberculosisVisit.RecordId is
'档案编号';
 comment on column TB_TuberculosisVisit.visitDate is
'随访时间';
 comment on column TB_TuberculosisVisit.treatmentMonthOrder is
'治疗月序';
 comment on column TB_TuberculosisVisit.supervisor is
'督导人员';
 comment on column TB_TuberculosisVisit.visitType is
'随访方式';
 comment on column TB_TuberculosisVisit.symptomSign is
'症状及体征';
 comment on column TB_TuberculosisVisit.otherSymptomSign is
'其他症状及体征';
 comment on column TB_TuberculosisVisit.smokePerDay is
'吸烟每天';
 comment on column TB_TuberculosisVisit.smokeDay is
'吸烟天';
 comment on column TB_TuberculosisVisit.drinkPerDay is
'饮酒每天';
 comment on column TB_TuberculosisVisit.drinkDay is
'饮酒天';
 comment on column TB_TuberculosisVisit.chemotherapy is
'化疗方案';
 comment on column TB_TuberculosisVisit.medicineUsage is
'药物用法';
 comment on column TB_TuberculosisVisit.medicineType is
'药品剂型';
 comment on column TB_TuberculosisVisit.missMedicineNum is
'漏服药次数';
 comment on column TB_TuberculosisVisit.medicineBadReaction is
'药物不良反应';
 comment on column TB_TuberculosisVisit.medicineBadReactionDesc is
'药物不良反应详情';
 comment on column TB_TuberculosisVisit.complicationComorbidity is
'并发症或合并症';
 comment on column TB_TuberculosisVisit.complicationComorbidityDesc is
'并发症或合并症详情';
 comment on column TB_TuberculosisVisit.referralDivision is
'转诊科别';
 comment on column TB_TuberculosisVisit.referralReason is
'转诊原因';
 comment on column TB_TuberculosisVisit.twoWeekVisitResult is
'2周内随访的结果';
 comment on column TB_TuberculosisVisit.processAdvice is
'处理意见';
 comment on column TB_TuberculosisVisit.nextDate is
'下次随访时间';
 comment on column TB_TuberculosisVisit.visitDoctor is
'评估医生签名';
 comment on column TB_TuberculosisVisit.stopTreatmentTime is
'停止治疗时间';
 comment on column TB_TuberculosisVisit.stopTreatmentReason is
'停止治疗原因';
 comment on column TB_TuberculosisVisit.shouldVisitNum is
'应访视次数';
 comment on column TB_TuberculosisVisit.actualVisitNum is
'实际访视次数';
 comment on column TB_TuberculosisVisit.shouldMedicineNum is
'应服药次数';
 comment on column TB_TuberculosisVisit.actualMedicineNum is
'实际服药次数';
 comment on column TB_TuberculosisVisit.assessDoctor is
'评估医生';
 comment on column TB_TuberculosisVisit.createUser is
'createUser';
 comment on column TB_TuberculosisVisit.createUnit is
'createUnit';
 comment on column TB_TuberculosisVisit.createDate is
'createDate';
 comment on column TB_TuberculosisVisit.lastModifyUnit is
'lastModifyUnit';
 comment on column TB_TuberculosisVisit.lastModifyUser is
'lastModifyUser';
 comment on column TB_TuberculosisVisit.lastModifyDate is
'lastModifyDate';

----------传染病报告卡添加结案字段-------------
alter table IDR_REPORT add finishstatus varchar2(1);
alter table IDR_REPORT add finishdate date;
alter table IDR_REPORT add finishreason varchar2(200);
comment on column IDR_REPORT.finishreason
  is '结案原因';
comment on column IDR_REPORT.finishstatus
  is '是否结案';
comment on column IDR_REPORT.finishdate
  is '结案时间';
  
alter table TB_TuberculosisFirstVisit add vertilation varchar2(1);
comment on column TB_TuberculosisFirstVisit.vertilation
  is '通风情况';