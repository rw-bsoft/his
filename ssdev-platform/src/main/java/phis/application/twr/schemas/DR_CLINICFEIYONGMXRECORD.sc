<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="DR_CLINICFEIYONGMXRECORD" tableName="DR_CLINICFEIYONGMXRECORD" alias="挂号费用明细记录">
    <!--(目前暂时先放在本地数据库)-->
    <item id="ID" alias="记录序号" type="string" length="16"  not-null="1" generator="assigned" pkey="true" width="160" hidden="true">
        <key>
            <rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
        </key>  
    </item>
    <item id="EMPIID" alias="EMPIID" type="string" length="32" hidden="true" />
    <item id="MZHM" alias="门诊号码" type="string" length="32"/>
    <item id="GUAHAOID" alias="挂号ID" type="string" length="30" fixed="true" />
    <item id="QUHAOMM" alias="取号密码" type="string" length="20" width="120" fixed="true"/>
    <item id="XIANGMUCDMC" alias="项目名称" type="string" length="20" width="120" fixed="true"/>
    <item id="XIANGMUGLMC" alias="项目GL" type="string" length="20" fixed="true" />
    <item id="XIANGMUGG" alias="项目GG" type="string" length="20" fixed="true" />
    <item id="XIANGMUGL" alias="项目GL" type="string" length="32" hidden="true" />
    <item id="ZILIJE" alias="自理金额" type="string" length="32"/>
    <item id="XIANGMUJX" alias="项目JX" type="string" length="20" width="120" fixed="true"/>
    <item id="XIANGMUCDDM" alias="项目CD代码" type="string" length="30" fixed="true" />
    <item id="SHULIANG" alias="数量" type="string" length="20" width="120" fixed="true"/>
    <item id="XIANGMUDW" alias="项目单位" type="string" length="32" hidden="true" />
    <item id="SHENGPIBH" alias="审批编号" type="string" length="32"/>
    <item id="YIBAODM" alias="医保代码" type="string" length="20" width="120" fixed="true"/>
    <item id="XIANGMUXH" alias="项目序号" type="string" length="32" hidden="true" />
    <item id="XIANGMUXJ" alias="项目XJ" type="string" length="32"/>
    <item id="ZIFEIBZ" alias="自费备注" type="string" length="20" width="120" fixed="true"/>
    <item id="JINE" alias="金额" type="string" length="32" hidden="true" />
    <item id="ZIFEIJE" alias="自费金额" type="string" length="32"/>
    <item id="ZIFUBL" alias="自付比例" type="string" length="20" width="120" fixed="true"/>
    <item id="DANJIA" alias="单价" type="string" length="32" hidden="true" />
    <item id="XIANGMUMC" alias="项目名称" type="string" length="32"/>
    <item id="FEIYONGLX" alias="费用类型" type="string" length="20" width="120" fixed="true"/>
    <item id="MINGXIXH" alias="明细序号" type="string" length="20" width="120" fixed="true"/>
    <item id="YIBAOZFBL" alias="医保自付比例" type="string" length="32" hidden="true" />
    <item id="YIBAODJ" alias="医保单价" type="string" length="32"/>
</entry>
