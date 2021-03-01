<!DOCTYPE html>
    <body>
		<ul id="toc">
			<#list toc as tocElement>
			<li class="pk-type flying"><a href="#${tocElement[0]}">${tocElement[1]}</a></li>
			</#list>
		</ul>

		<!--====================================================================================-->

    	<#if isModernMoves??>
    	<h2 id="mm">Move Modernization</h2>
    	<ul>
    		<#list moveUpdates as moveId, changes>
    		<#assign move = movesMod[moveId?index]>
    		<li>Made <strong>${move.name}</strong>
    		 
    		<#-- Apply type change -->
    		<#if changes[3]>
    		    <#-- Bridge of "and have" or "have" or nothing -->
	    		<#if changes[0] || changes[1] || changes[2]>
				be <span class="pk-type ${move.type.toString()?lower_case}">${move.type.toString()?upper_case}</span> and have
	    		<#else>
	    		be <span class="pk-type ${move.type.toString()?lower_case}">${move.type.toString()?upper_case}</span>
	    		</#if>
	    	<#else>
    		have
    		</#if>
    		
    		<#-- Apply power changes -->
    		<#if changes[0]>
    			<#-- Bridge of "," or "and" or nothing -->
	    		<#if changes[1] || changes[2]>
	    			<#if changes[1] && changes[2]>
	    			<strong>${move.power} power,</strong>
	    			<#else>
	    			<strong>${move.power} power </strong> and
	    			</#if>
	    		<#else>
	    		<strong>${move.power} power</strong>
	    		</#if>
    		</#if>
    		
    		<#-- Apply PP changes -->
    		<#if changes[1] && changes[2]>
    		<strong>${move.pp} pp</strong> and
    		<#elseif changes[1]>
    		<strong>${move.pp} pp</strong>
    		</#if>
    		
    		<#-- Apply Accuracy changes -->
    		<#if changes[2]>
    		<strong>${move.hitratio} accuracy</strong>
    		</#if>
    		</li>
    		</#list>
    	</ul>
    	</#if>

		<!--====================================================================================-->

    	<#list tweakMap>
    	<h2 id="pa">Patches Applied</h2>
    	<div>
    	<#items as patch, result>
	    	<#if result>
	    	<p><span class="success">${patch} successful!</span></p>
	    	<#else>
	    	<p><span class="error">${patch} unsuccessful.</span></p>
	    	</#if>
    	</#items>
    	</div>
    	</#list>


    	<#if natDexPatch??>
    	<h2>Patching for National Dex at Start of Game</h2>
    		<#switch natDexPatch>
    			<#case "noDexOffset">
 			    	<p><span class="error">Patch unsuccessful.</span></p>
 			    	<em>Reason: No Pokedex Offset found.</em>
    			<#break>
    			<#case "noScriptPtr">
    			    <p><span class="error">Patch unsuccessful.</span></p>
    			    <em>Reason: National Dex script not found on ROM.</em>
    			<#break>
    			<#case "fullRom">
    	    		<p><span class="error">Patch unsuccessful.</span></p>
    	    		<em>Reason: ROM is out of space for new routines.</em>
    			<#break>
    			<#case "success">
    				<p><span class="success">Patch successful!</span></p>
    			<#break>
    		</#switch>
    	</#if>

		<!--====================================================================================-->

    	<#if shuffledTypes??>
    		<h2 id="st">Shuffled Types</h2>
    		<table class="pk-table">
    		<tr><th>Old Type</th><th>New Type</th></tr>
    		<#list typeList as fmrType>
    			<tr>
    				<td><span class="pk-type ${fmrType?lower_case}">${fmrType}</span></td>
    				<td><span class="pk-type ${shuffledTypes[fmrType?index]?lower_case}">${shuffledTypes[fmrType?index]}</span></td>
    			</tr>
    		</#list>
    		</table>
    	</#if>

		<!--====================================================================================-->

		<#if updateEffectiveness??>
			<h2 id="fte">Fixing Type Effectiveness</h2>
			<ul>
				<li><strong>Replaced:</strong> <span class="pk-type poison">Poison</span> <em>super effective</em> vs <span class="pk-type bug">Bug</span> 
				=> <span class="pk-type ice">Ice</span> <em>not very effective</em> vs <span class="pk-type fire">Fire</span></li>
				<li><strong>Changed:</strong> <span class="pk-type bug">Bug</span> <em>super effective</em> vs <span class="pk-type poison">Poison</span>
                => <span class="pk-type bug">Bug</span> <em>not very effective</em> vs <span class="pk-type poison">Poison</span></li>
				<li><strong>Changed:</strong> <span class="pk-type psychic">Psychic</span> <em>immune</em> to <span class="pk-type ghost">Ghost</span>
                => <span class="pk-type ghost">Ghost</span> <em>super effective</em> vs <span class="pk-type psychic">Psychic</span></li>
			</ul>
		</#if>

		<!--====================================================================================-->

    	<#if logEvolutions?? && logEvolutions>
    		<h2 id="re">Randomized Evolutions</h2>
    		<ul>
    		<#list romHandler.getPokemon() as pkmn>
    		<#if pkmn?? && pkmn.evolutionsFrom?size gt 0>
    			<li>
    			${pkmn.name} now evolves into
					<ul>
					<#list pkmn.evolutionsFrom as evoFm>
						<li>
						${evoFm.to.name} - 
						<#switch evoFm.type>
							<#case "LEVEL">
							<#case "LEVEL_ATK_DEF_SAME">
							<#case "LEVEL_ATTACK_HIGHER">
				            <#case "LEVEL_DEFENSE_HIGHER">
								${evoFm.type} ${evoFm.extraInfo}
							<#break>
							<#case "LEVEL_HIGH_PV">
								LEVEL ${evoFm.extraInfo} when HIGH PERSONALITY VALUE
							<#break>
							<#case "LEVEL_LOW_PV">
								LEVEL ${evoFm.extraInfo} when LOW PERSONALITY VALUE
							<#break>
							<#case "LEVEL_MALE_ONLY">
								LEVEL ${evoFm.extraInfo} when MALE
							<#break>
							<#case "LEVEL_FEMALE_ONLY">
								LEVEL ${evoFm.extraInfo} when FEMALE
							<#break>
							<#case "LEVEL_WITH_OTHER">
								LEVEL with ${romHandler.getPokemon()[evoFm.extraInfo].name} in party
							<#break>
							<#case "LEVEL_ITEM_DAY">
								LEVEL holding ${romHandler.getItemNames()[evoFm.extraInfo]} during DAY
							<#break>
							<#case "LEVEL_ITEM_NIGHT">
								LEVEL holding ${romHandler.getItemNames()[evoFm.extraInfo]} during NIGHT
							<#break>
							<#case "LEVEL_WITH_MOVE">
								LEVEL while knowing ${romHandler.getMoves()[evoFm.extraInfo].name}
							<#break>
							<#case "STONE">
								${romHandler.getItemNames()[evoFm.extraInfo]}
							<#break>
							<#case "STONE_MALE_ONLY">
								${romHandler.getItemNames()[evoFm.extraInfo]} when MALE
							<#break>
							<#case "STONE_FEMALE_ONLY">
								${romHandler.getItemNames()[evoFm.extraInfo]} when FEMALE
							<#break>
							<#case "TRADE_ITEM">
								TRADE holding ${romHandler.getItemNames()[evoFm.extraInfo]}
							<#break>
							<#default>
								${evoFm.type}
							<#break>
						</#switch>
						</li>
					</#list>
					</ul>
    			</li>
    		</#if>
    		</#list>
    		</ul>
    		<h2 id="ep">New Evolution Paths</h2>
    		<table class="pk-table">
    		<#list basePokes as pkmn>
    			<tr>
    				<td>
    					<div class="pk-chain-element">${pkmn.name}</div>
    				</td>
    				<td>
					<#assign filteredEvos1=pkmn.getFilteredEvolutionsFrom()>
    				<#list filteredEvos1 as evo1>
    					<div class="pk-chain-element">${evo1.to.name}</div>
    				</#list>
    				</td>
    				<td>
    				<#list filteredEvos1 as evo>
    					<div style="min-height:34px;margin:5px 0;">
						<#assign filteredEvos2=evo.to.getFilteredEvolutionsFrom()>
    					<#list filteredEvos2 as evo2>
    						<div class="pk-chain-element">${evo2.to.name}</div>
    					</#list>
    					</div>
    				</#list>
    				</td>
    			</tr>
    		</#list>
    		</table>
    	</#if>

		<!--====================================================================================-->

    	<#if logPokemon?? && logPokemon>
    		<h2 id="ps">Pokemon Base Stats & Types</h2>
    		<table class="pk-table">
				<tr><th>NUM</th><th>NAME</th><th>TYPE</th><th>HP</th><th>ATK</th><th>DEF</th><th>SPE</th>
				<#if gen1>
					<th>SPEC</th>
				<#else>
					<th>SATK</th><th>SDEF</th>
				</#if>
				<th>TOTAL</th>
				<#-- Add Ability Header for each ability supported -->
				<#if romHandler.abilitiesPerPokemon() gt 0>
				<#list 1..romHandler.abilitiesPerPokemon() as abi>
					<th>ABILITY${abi}</th>
				</#list>
				</#if>
				<#if !gen1>
					<th>ITEM</th>
				</#if>
				<th>BIG</th></tr>
				<#list romHandler.getPokemon() as pkmn>
				<#if pkmn??>
					<#if pkmn.primaryType??>
					<#assign pkmnT1 = pkmn.primaryType.toString()>
					<#else>
					<#assign pkmnT1 = "???">
					</#if>
					<#if pkmn.secondaryType??>
					<#assign pkmnT2 = pkmn.secondaryType.toString()>
					<#else>
					<#assign pkmnT2 = "">
					</#if>
					<#if pkmn?is_even_item>
					<tr>
					<#else>
					<tr class="alt">
					</#if>
						<td>${pkmn.number}</td>
						<td class="left">${pkmn.name}</td>
						<td>
							<span class="pk-type ${pkmnT1?lower_case}">${pkmnT1?upper_case}</span>
							<#if pkmnT2 != "">
							<span class="pk-type ${pkmnT2?lower_case}">${pkmnT2?upper_case}</span>
							</#if>
						</td>
						<td>${pkmn.hp}</td>
						<td>${pkmn.attack}</td>
						<td>${pkmn.defense}</td>
						<td>${pkmn.speed}</td>
						<#if gen1>
							<td>${pkmn.special}</td>
							<td>${pkmn.gen1Bst()}</td>
						<#else>
							<td>${pkmn.spatk}</td>
							<td>${pkmn.spdef}</td>
							<td>${pkmn.bst()}</td>
						</#if>
						<#switch romHandler.abilitiesPerPokemon()>
						<#case 1>
							<td>${romHandler.abilityName(pkmn.ability1)}</td>
							<#break>	
						<#case 2>
							<td>${romHandler.abilityName(pkmn.ability1)}</td>
							<td>${romHandler.abilityName(pkmn.ability2)}</td>
							<#break>    										
						<#case 3>
							<td>${romHandler.abilityName(pkmn.ability1)}</td>
							<td>${romHandler.abilityName(pkmn.ability2)}</td>
							<td>${romHandler.abilityName(pkmn.ability3)}</td>
							<#break>
						</#switch> 
						<#if !gen1>
							<td>
							<#if pkmn.guaranteedHeldItem gt 0>
								${romHandler.getItemNames()[pkmn.guaranteedHeldItem]} (100%)
							</#if>
							<#if pkmn.commonHeldItem gt 0>
								${romHandler.getItemNames()[pkmn.commonHeldItem]} (common) <br />
							</#if>
							<#if pkmn.rareHeldItem gt 0>
								${romHandler.getItemNames()[pkmn.rareHeldItem]} (rare) <br />
							</#if>
							<#if pkmn.darkGrassHeldItem gt 0>
								${romHandler.getItemNames()[pkmn.darkGrassHeldItem]} (dark grass only)
							</#if>
							</td>
						</#if>
						<#if pkmn.isBigPoke(gen1)>
							<td>YES</td>
						<#else>
							<td></td>
						</#if>
					</tr>
				</#if>	
				</#list> 
    		</table>
    	<#else>
    		<h2 id="ps">Pokemon Base Stats & Types</h2>
    		<p>Unchanged.</p>
    	</#if>

		<!--====================================================================================-->

		<#if removeTradeEvo??>
			<h2 id="rte">Removing Trade Evolutions</h2>
			<ul>
			<#list removeTradeEvo as evo>
			<#if evo.type.name() == "LEVEL">
				<li>Made <strong>${evo.from.name}</strong> evolve into <strong>${evo.to.name}</strong> at level <strong>${evo.extraInfo}</strong></li>
			<#elseif evo.type.name() == "HAPPINESS">
				<li>Made <strong>${evo.from.name}</strong> evolve into <strong>${evo.to.name}</strong> by leveling up at high happiness</li>
			<#elseif evo.type.name() == "STONE">
				<li>Made <strong>${evo.from.name}</strong> evolve into <strong>${evo.to.name}</strong> using a <strong>${romHandler.getItemNames()[evo.extraInfo]}</strong></li>
			<#elseif evo.type.name() == "LEVEL_ITEM_DAY">
				<li>Made <strong>${evo.from.name}</strong> evolve into <strong>${evo.to.name}</strong> by leveling up holding <strong>${romHandler.getItemNames()[evo.extraInfo]}</strong></li>
			<#elseif evo.type.name() == "LEVEL_WITH_OTHER">
				<li>Made <strong>${evo.from.name}</strong> evolve into <strong>${evo.to.name}</strong> by leveling up with <strong>${romHandler.getPokemon()[evo.extraInfo].name}</strong> in the party</li>
			<#elseif evo.type.name() == "LEVEL_WITH_MOVE">
				<li>Made <strong>${evo.from.name}</strong> evolve into <strong>${evo.to.name}</strong> by leveling up knowing <strong>${romHandler.getMoves()[evo.extraInfo].name}</strong></li>
			<#else>
				<li>${evo.from.name} evo type is ${evo.type.name()}</li>
			</#if>
			</#list>
			</ul>
		</#if>

		<!--====================================================================================-->

		<#if condensedEvos??>
			<h2 id="cle">Condensed Level Evolutions</h2>
			<ul>
			<#list condensedEvos as evo>
				<li><strong>${evo.from.name}</strong> now evolves into <strong>${evo.to.name}</strong> at minimum level <strong>${evo.extraInfo}</strong></li>
			</#list>
			</ul>
		</#if>

		<!--====================================================================================-->

    	<#if logStarters??>
    	<#switch logStarters>
    		<#case "custom">
    			<h2 id="rs">Custom Starters</h2>
    		<#break>
    		<#case "random">
    			<h2 id="rs">Random Starters</h2>
    		<#break>
    		<#case "1or2evo">
    			<h2 id="rs">Random 1/2-Evolution Starters</h2>
    		<#break>
    		<#case "2evo">
    			<h2 id="rs">Random 2-Evolution Starters</h2>
    		<#break>
    	</#switch>
    	   <ul>
    		<#list startersList as pkmn>
    			<li>Set starter ${pkmn?counter} to <strong>${pkmn.name}</strong></li> 
    		</#list>
    		</ul>    		
    	</#if>

		<!--====================================================================================-->

		<#if logMoves?? && logMoves>
			<h2 id="md">Move Data</h2>
			<table class="moves-table">
				<tr><th>NUM</th><th>NAME</th><th>TYPE</th><th>POWER</th><th>ACC.</th><th>PP</th>
				<#if romHandler.hasPhysicalSpecialSplit()>
				<th>CATEGORY</th>
				</#if>
				<th>BIG</th></tr>
				<#list romHandler.getMoves() as mv>
				<#if mv??>
				    <#if mv.type??>
    				<#assign mvType = mv.type.toString()>
    				<#else>
    				<#assign mvType = "???">
    				</#if>
					<#if mv?is_even_item>
    				<tr>
    				<#else>
    				<tr class="alt">
    				</#if>
						<td>${mv.internalId}</td>
						<td class="left">${mv.name}</td>
						<td><span class="pk-type ${mvType?lower_case}">${mvType?upper_case}</span></td>
						<td>${mv.power}</td>
						<td>${mv.hitratio}</td>
						<td>${mv.pp}</td>
						<#if romHandler.hasPhysicalSpecialSplit()>
							<td>${mv.category.toString()}</td>
						</#if>
						<#if mv.isBigMove()>
							<td>YES</td>
						<#else>
							<td></td>
						</#if>
					</tr>
				</#if>
				</#list>
			</table>
		<#else>
			<h2 id="md">Move Data</h2>
			<p>Unchanged.</p>
		</#if>

		<!--====================================================================================-->

		<#if gameBreakingMoves??>
				<h2 id="gbm">Removed Game-Breaking Moves</h2>
				<ul>
				<#list romHandler.getGameBreakingMoves()?keys as name>
					<li>${name}</li>
				</#list>
				</ul>
		</#if>

		<!--====================================================================================-->
		
		<#if logPokemonMoves??>
			<#switch logPokemonMoves>
			<#case "metronome">
				<h2 id="pm">Pokemon Movesets</h2>
				<p>Metronome Only.</p>
				<#break>
			<#default>
				<h2 id="pm">Pokemon Movesets</h2>
				<#list romHandler.getMovesLearnt() as pkmn, moveList>
					<h3>${pkmn.number?string["000"]} ${pkmn.name}</h3>
					<ul class="moveset">
					<#list moveList as mv>
					<!-- This if statement could be pointless. It doesn't seem possible that
						 any move would be null. However, this preserves original functionality.  -->
					<#if romHandler.getMoves()[mv.move]??>
						<li><strong>${romHandler.getMoves()[mv.move].name}</strong><em>Lv ${mv.level}</em></li>
					<#else>
						<li><span class="error">Invalid move at level ${mv.level}</span></li>
					</#if>
					</#list>
					</ul>
				</#list>
			</#switch>
		<#else>
			<h2 id="pm">Pokemon Movesets</h2>
			<p>Unchanged.</p>
		</#if>

		<!--====================================================================================-->

		<#if originalTrainers??>
			<h2 id="tp">Trainers Pokemon</h2>
			<#list romHandler.getTrainers() as trainer>
			<div class="trainer-box">
				<div class="trainer">
					<span class="trainer-name"><em># ${trainer?counter}</em>${trainer.getLogName()}</span>
					<#if trainer.offset != trainer?counter && trainer.offset != 0>
                    	<em>@${trainer.stringOffset()}</em>
					</#if>
				</div>
				<em>Old Team</em>
				<ul class="old-trainer-pk">
				<#list originalTrainers[trainer?index].pokemon as tpk>
                    <li>${tpk.pokemon.name} <em>Lv${tpk.level}</em></li>
                </#list>
				</ul>
				<em>New Team</em>
				<ul class="new-trainer-pk">
				<#list trainer.pokemon as tpk>
                    <li>${tpk.pokemon.name} <em>Lv${tpk.level}</em></li>
                </#list>
				</ul>
			</div>
			</#list>
			<!-- Preserves spacing between trainer divs. Intentionally left empty -->
			<div class="clear"></div>
		<#else>
			<h2 id="tp">Trainers</h2>
			<p>Unchanged.</p>
		</#if>

		<!--====================================================================================-->

		<#if staticPokemon??>
			<h2 id="sp">Static Pokemon</h2>
			<ul>
			<#list staticPokemon as oldPoke, newPoke>
				<li>${oldPoke} => ${newPoke}</li>
			</#list>
			</ul>
		<#else>
			<h2 id="sp">Static Pokemon</h2>
			<p>Unchanged.</p>
		</#if>

		<!--====================================================================================-->

		<#if wildPokemon??>
			<h2 id="wp">Wild Pokemon</h2>
			<ul>
			<#list wildPokemon as encounterSet>
				<div class="wild-pk-set ${encounterSet.getDivClass()}">
					<#if encounterSet.displayName??>
					Set #${encounterSet?counter} - ${encounterSet.displayName} (rate=${encounterSet.rate})
					<#else>
					Set #${encounterSet?counter} (rate=${encounterSet.rate})
					</#if>
				</div>
				<ul class="pk-set-list ${encounterSet.getUlClass()}">
					<#list encounterSet.encounters as encounter>
					<li>${encounter.pokemon.name} 
						<#if encounter.maxLevel gt 0 && encounter.maxLevel != encounter.level>
						<em>Lvs ${encounter.level} - ${encounter.maxLevel}</em>
						<#else>
						<em>Lv ${encounter.level}</em>
						</#if>
					</li>
					</#list>
				</ul>
			</#list>
			</ul>
		<#else>
			<h2 id="wp">Wild Pokemon</h2>
			<p>Unchanged.</p>
		</#if>

		<!--====================================================================================-->
		
		<#if logTMMoves??>
			<#if TMTextFailure??>
			<p><span class="error">${TMTextFailure}</span></p>
			</#if>
			<#switch logTMMoves>
			<#case "metronome">
				<h2 id="tm">TM Moves</h2>
				<p>Metronome Only.</p>
				<#break>
			<#default>
				<h2 id="tm">TM Moves</h2>
				<ul class="tm-list">
				<#list romHandler.getTMMoves() as tm>
					<li><strong>TM${tm?counter?string["00"]}</strong> ${romHandler.getMoves()[tm].name}</li>
				</#list>
				</ul>
			</#switch>
		<#else>
			<h2 id="tm">TM Moves</h2>
			<p>Unchanged.</p>
		</#if>

		<!--====================================================================================-->
		
		<#if logTutorMoves??>
			<#if MoveTutorTextFailure??>
			<p><span class="error">${MoveTutorTextFailure}</span></p>
			</#if>
			<#switch logTutorMoves>
			<#case "metronome">
				<h2 id="mt">Move Tutor Moves</h2>
				<p>Metronome Only.</p>
				<#break>
			<#default>
				<h2 id="mt">Move Tutor Moves</h2>
				<ul class>
				<#list romHandler.getMoveTutorMoves() as tutor>
					<li>${romHandler.getMoves()[oldTutorMoves[tutor?index]].name} => ${romHandler.getMoves()[tutor].name}</li>
				</#list>
				</ul>
			</#switch>
		<#else>
			<h2 id="mt">Move Tutor Moves</h2>
			<p>Unchanged.</p>
		</#if>

		<!--====================================================================================-->

		<#if oldTrades??>
			<h2 id="igt">In-Game Trades</h2>
			<p>Trades are shown in pairs. New trades in <strong>bold</strong> on top, old trades below in <em>italics</em>.</p>
			<table class="trades-table">
				<tr><th>Requested</th><th>Given</th><th>Held Item</th></tr>
				<#list romHandler.getIngameTrades() as newTrade>
					<tr><td>${newTrade.requestedPokemon.name}</td><td>${newTrade.givenPokemon.name} (${newTrade.nickname})</td>
					<#if newTrade.item gt 0>
						<td>${romHandler.getItemNames()[newTrade.item]}</td>
					<#else>
						<td>${newTrade.item}</td>
					</#if>
					</tr>
					<tr class="alt"><td>${oldTrades[newTrade?index].requestedPokemon.name}</td><td>${oldTrades[newTrade?index].givenPokemon.name} (${oldTrades[newTrade?index].nickname})</td>
					<#if oldTrades[newTrade?index].item gt 0>
						<td>${romHandler.getItemNames()[oldTrades[newTrade?index].item]}</td>
					<#else>
						<td>${oldTrades[newTrade?index].item}</td>
					</#if>
					</tr>
				</#list>
			</table>
		</#if>
		<hr>
		<p>Randomization of <strong>${romHandler.getROMName()}</strong> completed.</p>
		<p>Time elapsed: ${elapsed?long?c}ms<p>
		<p>RNG Calls: ${rngCalls}<p>
		<p>RNG Seed: ${rngSeed?long?c}<p>
		<p>Settings: ${settingsString}<p>
    </body>
	<!-- HEAD section at end to enable proper FTL template colorization -->
	<head>
		<title>${romHandler.getROMName()} randomization log</title>
	    <meta charset="UTF-8"> 
	    <style type="text/css"> 
	    	<#include "log.css">
	    </style> 
	</head>
</html>