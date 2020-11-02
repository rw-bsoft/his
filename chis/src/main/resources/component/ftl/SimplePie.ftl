<?xml version="1.0" encoding="gb2312"?>
<chart caption="${title}" subcaption="${subTitle}" use3DLighting="1" palette="4" numberScaleValue="10000,10000" numberScaleUnit="万,亿" labelDisplay="none" xAxisName='' yAxisName='' showValues='1' legendPosition ="RIGHT">
	<#assign group=headers[0]>
	<#assign field=headers[1]>
	<#list rs as r>
		<#if group.dic??>
			<set label='${r[group.id + "_text"]!""}' value='
		<#else>
			<set label='${r[group.id]}' value='
		</#if>
		${r[field.id]}'
		<#if diggers??>
			link="javascript:FC_Click('${chartId}',${r_index},
			<#if diggers[group.id]??>
				'${group.id}')"
			</#if>
		</#if>		
		/>
	</#list>
    <trendlines>
      <line startValue='100000' color='ff0000' displayValue='' showOnTop='1'/>
    </trendlines>
	<styles>
		<definition>
			<style name='CaptionFont' type='font' size='12' color='666666' />
			<style name='yaxis' type='font' font='@宋体' size='10' bold='0' />
			<style name='xaxis' type='font' size='10' bold='0' />
			<style name='labels' type='font' size='10' color='#000000'/>
		</definition>
		<application>
				<apply toObject='caption' styles='CaptionFont' />
				<apply toObject='subcaption' styles='xaxis' />
				<apply toObject='XAXISNAME' styles='xaxis' />
				<apply toObject='YAXISNAME' styles='yaxis' />
				<apply toObject='DATALABELS' styles="labels"/>
				<apply toObject='TRENDVALUES' styles="labels"/> 
		</application>
	</styles>
</chart>	