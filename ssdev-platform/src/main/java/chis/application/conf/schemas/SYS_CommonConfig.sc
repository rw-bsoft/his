<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.conf.schemas.SYS_CommonConfig" alias="公共设置" version="1331800524540" filename="D:\Program Files\eclipse3.6\workspace\BSCHIS\WebRoot\WEB-INF\config\schema\sys/SYS_CommonConfig.xml">
  <item id="province" alias="省/自治区/直辖市" type="string" not-null="1">
    <dic id="chis.dictionary.areaGrid"  parentKey="0" sliceType="3"/>
  </item>
  <item id="city" alias="地级市/自治州/区" type="int" not-null="1">
    <dic id="chis.dictionary.areaGrid"/>
  </item>
  <item id="region" alias="县级市/县/区" type="string">
    <dic id="chis.dictionary.areaGrid"/>
  </item>
</entry>





