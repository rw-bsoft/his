<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.conf.schemas.SYS_ZookeeperConfig" alias="PIX服务及接口设置" >
  <item id="ifNeedPix" alias="是否调用平台PIX服务" type="String" colspan="2" not-null="1" length="1">
  	<dic id="chis.dictionary.yesOrNo"/>
  </item>
  <item id="phisActiveYW" alias="是否启用基层医疗" type="String" colspan="2" not-null="1" length="1">
  	<dic id="chis.dictionary.yesOrNo"/>
  </item>
  <item id="KLX" alias="建档默认卡类型" type="String" colspan="2" not-null="1" length="1">
  	<dic id="chis.dictionary.card"/>
  </item>
  <!--
  <item id="zkAddress" alias="Zookeeper地址" type="string" not-null="1"/>
  <item id="zkPort" alias="Zookeeper端口" type="int" not-null="1"/>
  -->
</entry>
