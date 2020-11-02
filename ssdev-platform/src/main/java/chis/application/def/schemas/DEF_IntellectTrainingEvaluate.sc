<?xml version="1.0" encoding="UTF-8"?>
<entry alias="智力残疾训练记录" sort="evaluateStage,id">
	<item id="id" alias="主键" type="string" length="16" not-null="1" generator="assigned" pkey="true" hidden="true"> 
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="defId" alias="登记号" type="string" length="16" hidden="true" display="2"/>
	<item id="visitDate" alias="评估日期" type="date" maxValue="%server.date.today" not-null="1" colspan="2" />
	<item id="fanshen" alias="翻身" type="int" minValue="0" maxValue="2"  not-null="1" display="2"/>
	<item id="zuo" alias="坐" type="int" enableKeyEvents="true" maxValue="2"  minValue="0" not-null="1" display="2"/>
	<item id="pa" alias="爬" type="int" minValue="0" maxValue="2"  enableKeyEvents="true"  not-null="1" display="2"/>
	<item id="zhan" alias="站" type="int" minValue="0" maxValue="2"  enableKeyEvents="true"  not-null="1" display="2"/>
	<item id="buxing" alias="步行" type="int" minValue="0" maxValue="2"  enableKeyEvents="true"  not-null="1" display="2"/>
	<item id="sxtj" alias="上下台阶" type="int" minValue="0" maxValue="2" enableKeyEvents="true"  not-null="1" display="2"/>
	<item id="pao" alias="跑" type="int" minValue="0" maxValue="2"  enableKeyEvents="true"  not-null="1" display="2"/>
	<item id="ssqw" alias="伸手取物" type="int" minValue="0" maxValue="2"  enableKeyEvents="true"  not-null="1" display="2"/>
	<item id="niequ" alias="捏取" type="int" minValue="0" maxValue="2"  enableKeyEvents="true"  not-null="1" display="2"/>
	<item id="ninggai" alias="拧盖" type="int" minValue="0" maxValue="2"  enableKeyEvents="true"  not-null="1" display="2"/>
	<item id="xkz" alias="系扣子" type="int" minValue="0" maxValue="2"  enableKeyEvents="true"  not-null="1" display="2"/>
	<item id="czz" alias="穿珠子" type="int" minValue="0" maxValue="2"  enableKeyEvents="true"  not-null="1" display="2"/>
	<item id="zhezhi" alias="折纸" type="int" minValue="0" maxValue="2"  enableKeyEvents="true"  not-null="1" display="2"/>
	<item id="zswt" alias="注视物体" type="int" minValue="0" maxValue="2"  enableKeyEvents="true"  not-null="1" display="2"/>
	<item id="zsydwt" alias="追视移动物体" type="int" minValue="0" maxValue="2"  enableKeyEvents="true"  not-null="1" display="2"/>
	<item id="fbwd" alias="分辨味道" type="int" minValue="0" maxValue="2"  enableKeyEvents="true"  not-null="1" display="2"/>
	<item id="fbqw" alias="分辨气味" type="int" minValue="0" maxValue="2"  enableKeyEvents="true"  not-null="1" display="2"/>
	<item id="fbcjsy" alias="分辨常见声音" type="int" minValue="0" maxValue="2"  enableKeyEvents="true"  not-null="1" display="2"/>
	<item id="cjfb" alias="触觉分辨" type="int" minValue="0" maxValue="2"  enableKeyEvents="true"  not-null="1" display="2"/>
	<item id="rswtdcz" alias="认识物体存在" type="int" minValue="0" maxValue="2"  enableKeyEvents="true"  not-null="1" display="2"/>
	<item id="wpgl" alias="物品归类" type="int" minValue="0" maxValue="2"  enableKeyEvents="true"  not-null="1" display="2"/>
	<item id="rscjlx" alias="认识常见联系" type="int" minValue="0" maxValue="2"  enableKeyEvents="true"  not-null="1" display="2"/>
	<item id="rsys" alias="认识颜色" type="int" minValue="0" maxValue="2"  enableKeyEvents="true"  not-null="1" display="2"/>
	<item id="rsfw" alias="认识方位" type="int" minValue="0" maxValue="2"  enableKeyEvents="true"  not-null="1" display="2"/>
	<item id="rsxz" alias="认识形状" type="int" minValue="0" maxValue="2"  enableKeyEvents="true"  not-null="1" display="2"/>
	<item id="fbyw" alias="分辨有无" type="int" minValue="0" maxValue="2"  enableKeyEvents="true"  not-null="1" display="2"/>
	<item id="rsscsg" alias="认识蔬菜水果" type="int" minValue="0" maxValue="2"  enableKeyEvents="true"  not-null="1" display="2"/>
	<item id="zdtqqk" alias="知道天气情况" type="int" minValue="0" maxValue="2"  enableKeyEvents="true"  not-null="1" display="2"/>
	<item id="zdyggx" alias="知道因果关系" type="int" minValue="0" maxValue="2"  enableKeyEvents="true"  not-null="1" display="2"/>
	<item id="dianshu" alias="点数" type="int" minValue="0" maxValue="2"  enableKeyEvents="true"  not-null="1" display="2"/>
	<item id="rssj" alias="认识时间" type="int" minValue="0" maxValue="2"  enableKeyEvents="true"  not-null="1" display="2"/>
	<item id="rsqb" alias="认识钱币" type="int" minValue="0" maxValue="2"  enableKeyEvents="true"  not-null="1" display="2"/>
	<item id="zdzjmz" alias="知道自己名字" type="int" minValue="0" maxValue="2"  enableKeyEvents="true"  not-null="1" display="2"/>
	<item id="fcjddml" alias="服从简单命令" type="int" minValue="0" maxValue="2"  enableKeyEvents="true"  not-null="1" display="2"/>
	<item id="bdxq" alias="表达需求" type="int" minValue="0" maxValue="2"  enableKeyEvents="true"  not-null="1" display="2"/>
	<item id="sjddyj" alias="说简单短句" type="int" minValue="0" maxValue="2"  enableKeyEvents="true"  not-null="1" display="2"/>
	<item id="yyjl" alias="语言交流" type="int" minValue="0" maxValue="2"  enableKeyEvents="true"  not-null="1" display="2"/>
	<item id="sxdjbnl" alias="书写基本能力" type="int" minValue="0" maxValue="2"  enableKeyEvents="true"  not-null="1" display="2"/>
	<item id="nzswc" alias="拿着食物吃" type="int" minValue="0" maxValue="2"  enableKeyEvents="true"  not-null="1" display="2"/>
	<item id="ycjc" alias="用餐具吃" type="int" minValue="0" maxValue="2"  enableKeyEvents="true"  not-null="1" display="2"/>
	<item id="ycjh" alias="用餐具喝" type="int" minValue="0" maxValue="2"  enableKeyEvents="true"  not-null="1" display="2"/>
	<item id="xbzl" alias="小便自理" type="int" minValue="0" maxValue="2"  enableKeyEvents="true"  not-null="1" display="2"/>
	<item id="dbzl" alias="大便自理" type="int" minValue="0" maxValue="2"  enableKeyEvents="true"  not-null="1" display="2"/>
	<item id="tyf" alias="脱衣服" type="int" minValue="0" maxValue="2"  enableKeyEvents="true"  not-null="1" display="2"/>
	<item id="cyf" alias="穿衣服" type="int" minValue="0" maxValue="2"  enableKeyEvents="true"  not-null="1" display="2"/>
	<item id="cxw" alias="穿鞋袜" type="int" minValue="0" maxValue="2"  enableKeyEvents="true"  not-null="1" display="2"/>
	<item id="shuaya" alias="刷牙" type="int" minValue="0" maxValue="2"  enableKeyEvents="true"  not-null="1" display="2"/>
	<item id="xilian" alias="洗脸" type="int" minValue="0" maxValue="2"  enableKeyEvents="true"  not-null="1" display="2"/>
	<item id="xishou" alias="洗手" type="int" minValue="0" maxValue="2"  enableKeyEvents="true"  not-null="1" display="2"/>
	<item id="xijiao" alias="洗脚" type="int" minValue="0" maxValue="2"  enableKeyEvents="true"  not-null="1" display="2"/>
	<item id="gbz" alias="盖被子" type="int" minValue="0" maxValue="2"  enableKeyEvents="true"  not-null="1" display="2"/>
	<item id="dblc" alias="叠被理床" type="int" minValue="0" maxValue="2"  enableKeyEvents="true"  not-null="1" display="2"/>
	<item id="rsjjhj" alias="认识家居环境" type="int" minValue="0" maxValue="2"  enableKeyEvents="true"  not-null="1" display="2"/>
	<item id="zdzj" alias="知道自己" type="int" minValue="0" maxValue="2"  enableKeyEvents="true"  not-null="1" display="2"/>
	<item id="rsssdr" alias="认识熟识人" type="int" minValue="0" maxValue="2"  enableKeyEvents="true"  not-null="1" display="2"/>
	<item id="rsjthj" alias="认识家庭环境" type="int" minValue="0" maxValue="2"  enableKeyEvents="true"  not-null="1" display="2"/>
	<item id="zdjjaq" alias="知道居家安全" type="int" minValue="0" maxValue="2"  enableKeyEvents="true"  not-null="1" display="2"/>
	<item id="rsggss" alias="认识公共设施" type="int" minValue="0" maxValue="2"  enableKeyEvents="true"  not-null="1" display="2"/>
	<item id="cjjthd" alias="参加集体活动" type="int" minValue="0" maxValue="2"  enableKeyEvents="true"  not-null="1" display="2"/>
	<item id="daqcs" alias="懂安全常识" type="int" minValue="0" maxValue="2"  enableKeyEvents="true"  not-null="1" display="2"/>
 
  
	<item id="score" alias="整体评估分数" type="int" fixed="true" not-null="1"  display="2"/>
	
	
	<item id="evaluateStage" alias="评估阶段" type="int" fixed="true" not-null="1"  display="2">
		<dic>
			<item key="1" text="初次评估" />
			<item key="2" text="不定期评估" />
		</dic>
	</item>
	<item id="visitUser" alias="康复指导员" type="string" length="20" defaultValue="%user.userId" colspan="2" not-null="1" display="2">
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true"   parentKey="%user.manageUnit.id"/>
	</item>
	<item id="content" alias="记录内容" length="2000" not-null="1" colspan="6" display="2"/>
	
	<item id="inputUnit" alias="录入单位" type="string" length="20"
		update="false" fixed="true" width="165"
		defaultValue="%user.manageUnit.id" display="0">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="inputUser" alias="录入人" type="string" length="20"
		update="false" fixed="true" defaultValue="%user.userId" display="0">
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true"   parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="inputDate" alias="录入时间" type="datetime"  xtype="datefield" update="false"
		fixed="true" defaultValue="%server.date.today" display="0">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>	
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		defaultValue="%user.userId" display="0">
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20"
		width="180" display="0" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield"
		defaultValue="%server.date.today" display="0">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
</entry>
