<?xml version="1.0" encoding="gb2312"?>
<chart caption="${title}" subcaption="${subTitle}"  use3DLighting="1" numberScaleValue="10000,10000" numberScaleUnit="万,亿" labelDisplay="none" xAxisName='' yAxisName='' showValues='1' legendPosition ="RIGHT">
	<#assign r=rs[0]>
	<#list headers as h>
		<set label='${h.alias}' value='${r[h.id]}'
		<#if diggers[h.id]??>
			link="javascript:FC_Click('${chartId}',0,'${h.id}')"
		</#if>/>
	</#list>
    <trendlines>
      <line startValue='860' color='ff0000' displayValue='' showOnTop='1'/>
    </trendlines>
	<styles>
		<definition>
			<style name='caption' type='font' size='12' color='666666'/>
			<style name='subcaption' type='font' size='11' color='666666' bold='0'/>
			<style name='yaxis' type='font' font='@宋体' size='10' bold='0' />
			<style name='xaxis' type='font' size='10' bold='0' />
			<style name='labels' type='font' size='10' color='#000000'/>
		</definition>
		<application>
				<apply toObject='caption' styles='caption' />
				<apply toObject='subcaption' styles='subcaption' />
				<apply toObject='XAXISNAME' styles='xaxis' />
				<apply toObject='YAXISNAME' styles='yaxis' />
				<apply toObject='DATALABELS' styles="labels"/>
				<apply toObject='TRENDVALUES' styles="labels"/> 
		</application>
	</styles>
</chart>	