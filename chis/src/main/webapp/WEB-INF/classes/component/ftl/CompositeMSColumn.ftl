<?xml version="1.0" encoding="gb2312"?>
<#assign group=headers[0]>
<chart caption="${title}" animation="1"  showLegend="1" legendScrollBtnColor='cccccc' legendScrollBarColor='eeeeee' canvasBorderThickness="1" canvasBorderAlpha="60" showBorder="0" subcaption="${subTitle}"  numVDivLines='10' useRoundEdges='0' use3DLighting="1" showLegend='0' maxColWidth="30" numberScaleValue="10000,10000" numberScaleUnit="��,��" labelDisplay="Stagger"  yAxisName='' showValues='1' legendPosition="RIGHT" >
	<categories>
	<#list rs as r>
		<#if r_index = limit>
			<#break>
		</#if>
		<#if r[group.id]!="sum">
			<#if group.dic??>
				<category label='${r[group.id + "_text"]!""}'/>
			<#else>
				<category label='${r[group.id]!""}'/>
			</#if>
		</#if>
	</#list>
	</categories>
	<#list headers as h>
		<#if h.func?? && !(h.hidden??)>
			<dataset seriesName='${h.alias}'>
			<#list rs as r>
				<#if r_index = limit>
					<#break>
				</#if>
				<#if r[h.id]??>
				<set value='${r[h.id]}'
				<#if diggers??>
					link="javascript:FC_Click('${chartId}',${r_index},
					<#if diggers['match']??>
						'${diggers.match}'
					<#else>
						<#if diggers[h.id]??>
							'${h.id}'
						<#else>
							'${group.id}'
						</#if>
					</#if>
					)"
				</#if>
				/>	
				<#else>
					<set value='0'/>
				</#if>
			</#list>
			</dataset>
		</#if>
	</#list>
	<styles>
		<definition>
			<style name='caption' type='font' size='12' color='666666'/>
			<style name='subcaption' type='font' size='11' color='666666' bold='0'/>
			<style name='yaxis' type='font' font='@����' size='10' bold='0' />
			<style name='xaxis' type='font' size='10' bold='0' />
			<style name='labels' type='font' size='10' color='#000000'/>
		</definition>
		<application>
				<apply toObject='caption' styles='caption' />
				<apply toObject='subcaption' styles='subcaption' />
				<apply toObject='XAXISNAME' styles='xaxis' />
				<apply toObject='YAXISNAME' styles='yaxis' />
				<apply toObject='DATALABELS' styles="labels"/>
				<apply toObject='Legend' styles="labels"/>
				<apply toObject='TRENDVALUES' styles="labels"/> 
		</application>
	</styles>
</chart>	