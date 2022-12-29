/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2jmobius.gameserver.network.clientpackets.huntingzones;

import org.l2jmobius.commons.threads.ThreadPool;
import org.l2jmobius.gameserver.data.xml.TimedHuntingZoneData;
import org.l2jmobius.gameserver.enums.TeleportWhereType;
import org.l2jmobius.gameserver.instancemanager.InstanceManager;
import org.l2jmobius.gameserver.instancemanager.MapRegionManager;
import org.l2jmobius.gameserver.instancemanager.ZoneManager;
import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.holders.TimedHuntingZoneHolder;
import org.l2jmobius.gameserver.model.variables.PlayerVariables;
import org.l2jmobius.gameserver.model.zone.ZoneId;
import org.l2jmobius.gameserver.model.zone.ZoneType;
import org.l2jmobius.gameserver.model.zone.type.TimedHuntingZone;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.clientpackets.ClientPacket;
import org.l2jmobius.gameserver.network.serverpackets.SystemMessage;
import org.l2jmobius.gameserver.network.serverpackets.huntingzones.TimedHuntingZoneExit;

/**
 * @author Index
 */
public class ExTimedHuntingZoneLeave implements ClientPacket
{
	@Override
	public void run(GameClient client)
	{
		final Player player = client.getPlayer();
		if (player == null)
		{
			return;
		}
		
		if (player.isInCombat())
		{
			player.sendPacket(new SystemMessage(SystemMessageId.YOU_CANNOT_TELEPORT_IN_BATTLE));
			return;
		}
		
		TimedHuntingZoneHolder huntingZone = player.getTimedHuntingZone();
		final Location exit = huntingZone != null ? huntingZone.getExitLocation() != null ? huntingZone.getExitLocation() : InstanceManager.getInstance().getInstance(huntingZone.getInstanceId()).getExitLocation(player) : MapRegionManager.getInstance().getTeleToLocation(player, TeleportWhereType.TOWN);
		if (huntingZone != null)
		{
			player.getVariables().set(PlayerVariables.LAST_HUNTING_ZONE_ID, huntingZone.getZoneId());
			player.teleToLocation(exit, InstanceManager.getInstance().getInstance(0));
			startScheduleTask(player, huntingZone);
		}
		else
		{
			huntingZone = TimedHuntingZoneData.getInstance().getHuntingZone(player.getVariables().getInt(PlayerVariables.LAST_HUNTING_ZONE_ID, -1));
			if (huntingZone != null)
			{
				startScheduleTask(player, huntingZone);
			}
			else
			{
				player.sendPacket(new TimedHuntingZoneExit(0));
			}
		}
	}
	
	private void startScheduleTask(Player player, TimedHuntingZoneHolder holder)
	{
		ThreadPool.schedule(() -> additionalCheck(player, holder), 2000);
	}
	
	private void additionalCheck(Player player, TimedHuntingZoneHolder timedHuntingZoneHolder)
	{
		final ZoneType currentZone = ZoneManager.getInstance().getZone(timedHuntingZoneHolder.getEnterLocation(), TimedHuntingZone.class);
		if (currentZone != null)
		{
			if (currentZone.isCharacterInZone(player))
			{
				currentZone.removeCharacter(player);
			}
			else if (player.isInsideZone(ZoneId.TIMED_HUNTING))
			{
				player.sendPacket(new TimedHuntingZoneExit(timedHuntingZoneHolder.getZoneId()));
				player.setInsideZone(ZoneId.TIMED_HUNTING, false);
			}
		}
	}
}
