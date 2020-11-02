<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.idr.schemas.IDR_Report" alias="TB_传染病报告卡">
	<item id="RecordID" alias="记录编号" type="string" length="16" pkey="true" fixed="true" hidden="true" generator="assigned" display="0">
		<key>
		  <rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item ref="b.personName" display="1" queryable="true" />
	<item ref="b.sexCode" display="1" queryable="true" />
	<item ref="b.birthday" display="1" queryable="true" />
	<item ref="b.idCard" display="1" queryable="true" />
	<item ref="b.mobileNumber" display="1" queryable="true" />
	<item ref="c.manaUnitId" display="1" queryable="true" />
	<item ref="c.regionCode" display="1" queryable="true" width="300"/>
	<item id="empiId" alias="empiId" type="string" display="0" length="32" not-null="1"/>
	<item id="phrId" alias="档案编号" type="string" display="0"  length="30"/>
	<item id="cardCategory" defaultValue="1" alias="报卡类别" length="1" not-null="1">
	  <dic>
		<item key="1" text="初次报告" />
		<item key="2" text="订正报告" />
		</dic>
	</item>
	 
	<item id="parentsName" alias="患儿家长姓名" length="30"/>
	<item id="fullAge" alias="实足年龄" length="20" fixed="true" />
	<item id="patientJob" queryable="true" alias="患者职业" length="2" not-null="1" >
	  <dic>
		<item key="1" text="幼托儿童" />
		<item key="2" text="散居儿童 " />
		<item key="3" text="学生（大中小学）" />
		<item key="4" text="教师" />
		<item key="5" text="保育员及保姆" />
		<item key="6" text="餐饮食品业 " />
		<item key="7" text="商业服务" />
		<item key="8" text="医务人员" />
		<item key="9" text="工人" />
		<item key="10" text="民工" />
		<item key="11" text="农民" />
		<item key="12" text="牧民" />
		<item key="13" text="渔（船） 民" />
		<item key="14" text="干部职员 " />
		<item key="15" text="离退人员 " />
		<item key="16" text="家务及待业" />
		<item key="17" text="其他" />
		<item key="18" text="不详" />
		</dic>
	</item>	
	<item id="otherPatientJob" alias="其它职业" length="20"/>
	<item id="birthPlace" queryable="true" alias="病人属于" length="1" not-null="1">
	  <dic>
		<item key="1" text="本县区" />
		<item key="2" text="本市其他县区" />
		<item key="3" text="本省其它地市" />
		<item key="4" text="外省" />
		<item key="5" text="港澳台" />
		<item key="9" text="外籍" />
	  </dic>
	</item>
	<item id="casemixCategory1" queryable="true" alias="病例分类1" length="1" not-null="1">
	  <dic>
		<item key="1" text="疑似病例" />
		<item key="2" text="临床诊断病例" />
		<item key="3" text="实验室确诊病例" />
		<item key="4" text="病原携带者" />
	  </dic>
	</item>
	 
	<item id="casemixCategory2" queryable="true" alias="病例分类2" length="1">
	  <dic>
		<item key="1" text="急性" />
		<item key="2" text="慢性（乙型肝炎、血吸虫病填写）" />
	  </dic>
	</item>
	 
	<item id="dateAccident" alias="发病日期"   queryable="true"  type="date" not-null="1" maxValue="%server.date.today"/>
	<item id="diagnosedDate" alias="诊断日期" type="date" maxValue="%server.date.today"/>
	<item id="deathDate" alias="死亡日期" type="date" maxValue="%server.date.today"/>
	<item id="categoryAInfectious" alias="甲类传染病" length="50" hidden="true" enableKeyEvents="true">
	  <dic render="LovCombo">
		<item key="1" text="鼠疫" />
		<item key="2" text="霍乱" />
	  </dic>
	</item>
	 
	<item id="categoryBInfectious" alias="乙类传染病" length="100" display="0" hidden="true" width="150" enableKeyEvents="true" defaultValue="13">
	  <dic render="LovCombo">
		<item key="1" text="传染性非典型肺炎" />
		<item key="2" text="艾滋病" />
		<item key="3" text="病毒性肝炎（  甲型、  乙型、  丙型、  戍型、  未分型）" />
		<item key="4" text="脊髓灰质炎" />
		<item key="5" text="人感染高致病性禽流感" />
		<item key="6" text="麻疹" />
		<item key="7" text="流行性出血热" />
		<item key="8" text="狂犬病" />
		<item key="9" text="流行性乙型脑炎" />
		<item key="10" text="登革热" />
		<item key="11" text="炭疽" />
		<item key="12" text="痢疾" />
		<item key="13" text="肺结核" />
		<item key="14" text="伤寒" />
		<item key="15" text="流行性脑脊髓膜炎" />
		<item key="16" text="百日咳" />
		<item key="17" text="白喉" />
		<item key="18" text="新生儿破伤风" />
		<item key="19" text="猩红热" />
		<item key="20" text="布鲁氏菌病" />
		<item key="21" text="淋病" />
		<item key="22" text="梅毒" />
		<item key="23" text="钩端螺旋体病" />
		<item key="24" text="血吸虫病" />
		<item key="25" text="疟疾" />
		</dic>
	</item>
	<item id="categoryCInfectious" alias="丙类传染病" length="50" display="0" hidden="true" width="150" enableKeyEvents="true">
	  <dic render="LovCombo">
		<item key="1" text="流行性感冒" />
		<item key="2" text="流行性腮腺炎" />
		<item key="3" text="风疹" />
		<item key="4" text="急性出血性结膜炎" />
		<item key="5" text="麻风病" />
		<item key="6" text="流行性和地方性斑疹伤寒" />
		<item key="7" text="黑热病" />
		<item key="8" text="包虫病" />
		<item key="9" text="丝虫病" />
		<item key="10" text="除霍乱、细菌性和阿米巴性痢疾、伤寒和副伤寒以外的感染性腹泻病" />
	  </dic>
	</item>
	<item id="viralHepatitis" alias="病毒性肝炎" display="0" hidden="true" length="50">
	  <dic>
		<item key="1" text="甲型" />
		<item key="2" text="乙型" />
		<item key="3" text="丙型" />
		<item key="4" text="戍型" />
		<item key="5" text="未分型" />
	  </dic>
	</item>
	  	
	<item id="anthrax" alias="炭疽" type="string" display="0" hidden="true" length="1">
	  <dic>
		<item key="1" text="肺炭疽" />
		<item key="2" text="皮肤炭疽" />
		<item key="3" text="未分型" />
	  </dic>
	</item>
	<item id="dysentery" alias="痢疾" type="string" display="0" hidden="true" length="1">
	  <dic>
		<item key="1" text="细菌性" />
		<item key="2" text="阿米巴性" />
	  </dic>
	</item>
	<item id="phthisis" alias="肺结核" type="string" length="1">
	  <dic>
		<item key="1" text="涂阳" />
		<item key="2" text="仅培阳" />
		<item key="3" text="菌阴" />
		<item key="4" text="未痰检" />
	  </dic>
	</item>	
	<item id="typhia" alias="伤寒" type="string" display="0" hidden="true" length="1">
	  <dic>
		<item key="1" text="伤寒" />
		<item key="2" text="副伤寒" />
	  </dic>
	</item>
	<item id="syphilis" alias="梅毒" type="string" display="0" hidden="true" length="1">
	  <dic>
		<item key="1" text="Ⅰ期" />
		<item key="2" text="Ⅱ期" />
		<item key="3" text="Ⅲ期" />
		<item key="4" text="胎传" />
		<item key="5" text="隐性" />
	  </dic>
	</item>
	<item id="malaria" alias="疟疾" type="string" display="0" hidden="true" length="1">
	  <dic>
		<item key="1" text="间日疟" />
		<item key="2" text="恶性疟" />
		<item key="3" text="未分型" />
	  </dic>
	</item>
	 
	<item id="otherCategoryInfectious" alias="其他传染病" display="0" hidden="true" length="200" colspan="2" width="150" enableKeyEvents="true"/>
	 
	<item id="correctionProblems" alias="订正病名" length="20"/>
	 
	<item id="returnReason" alias="退卡原因" length="20" colspan="2" width="150"/>
	 
	<item id="unitPhone" alias="联系电话" length="20" not-null="1"/>
	 
	<item id="reportDoctor" alias="报告医生" type="string" length="20" not-null="1"  defaultValue="%user.userId" display="2">
		<dic id="chis.dictionary.user05" parentKey="%user.manageUnit.id" render="Tree" onlySelectLeaf="true" keyNotUniquely="true"/>
	</item>
	<item id="reportUnit" alias="填报单位" queryable="true"  fixed="false" type="string" length="20" width="180"   defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  lengthLimit="9"  render="Tree"  rootVisible = "true"
		  parentKey="%user.manageUnit.id" />
	</item>
	 
	<item id="fillDate" alias="填卡日期" queryable="true"  type="date" not-null="1" maxValue="%server.date.today"/>
	
	<item id="comments" alias="备注" length="200" colspan="2"/>
	<item id="status" alias="状态" length="1" hidden="true"
		defaultValue="0">
		<dic>
		<item key="0" text="正常" />
		<item key="1" text="已注销" />
		</dic>
	</item>		<item id="finishStatus" alias="是否结案" length="1" defaultValue="0">		<dic id="chis.dictionary.finishStatus"/>	</item>	<item id="finishDate" alias="结案日期" type="date" />	<item id="finishReason" alias="结案原因" length="200"/>	
	<item id="cancellationUser" alias="注销人" type="string" length="20"
		hidden="true">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
		  parentKey="%user.manageUnit.id" />
	</item>
	<item id="cancellationDate" alias="注销日期" type="date" hidden="true" />
	<item id="cancellationUnit" alias="注销单位" type="string" length="20"
		width="180" hidden="true">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
		  parentKey="%user.manageUnit.id" />
	</item>
	<item id="cancellationReason" alias="注销原因" type="string" length="1" hidden="true" colspan="2">
		<dic>
		<item key="1" text="死亡" />
		<item key="2" text="迁出" />
		<item key="3" text="失访" />
		<item key="4" text="拒绝" />
		<item key="9" text="其他" />
		</dic>
	</item>
	<item id="deadReason" alias="死亡原因" type="string" fixed="true" hidden="true"
		length="100" display="2" colspan="3" anchor="100%" />
	   
	<item id="MS_BRZD_JLBH" alias="门诊诊断记录编号" type="string" length="1" hidden="true"/>	
	<item id="createUnit" alias="录入机构" type="string" length="20"
		width="180" fixed="true" update="false" defaultValue="%user.manageUnit.id" display="0">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
		  parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="createUser" alias="录入人" type="string" length="20"
		update="false" fixed="true" defaultValue="%user.userId" display="0">
		<dic id="chis.dictionary.Personnel"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="createDate" alias="录入时间" type="datetime"  xtype="datefield" update="false"
		fixed="true" defaultValue="%server.date.today" display="0">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改机构" type="string" length="20" display="0" width="180" hidden="true" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		defaultValue="%user.userId" display="0">
		<dic id="chis.dictionary.Personnel"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield"
		defaultValue="%server.date.today" display="0">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>

	<relations>
		<relation type="parent" entryName="chis.application.mpi.schemas.MPI_DemographicInfo" />
		<relation type="children" entryName="chis.application.hr.schemas.EHR_HealthRecord">
			<join parent="phrId" child="phrId" />
		</relation>
	</relations>

</entry>