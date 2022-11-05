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
package org.l2jmobius.gameserver.network.clientpackets;

import org.l2jmobius.commons.network.ReadablePacket;
import org.l2jmobius.gameserver.enums.SkillEnchantType;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.serverpackets.ExEnchantSkillInfoDetail;
import org.l2jmobius.gameserver.util.SkillEnchantConverter;

/**
 * @author -Wooden-
 */
public class RequestExEnchantSkillInfoDetail implements ClientPacket
{
	private SkillEnchantType _type;
	private int _skillId;
	private int _skillLevel;
	
	@Override
	public void read(ReadablePacket packet)
	{
		_type = SkillEnchantType.values()[packet.readInt()];
		_skillId = packet.readInt();
		_skillLevel = packet.readInt();
	}
	
	@Override
	public void run(GameClient client)
	{
		final Player player = client.getPlayer();
		if (player == null)
		{
			return;
		}
		
		final int skillLevel;
		final int skillSubLevel;
		if (_skillLevel < 100)
		{
			skillLevel = _skillLevel;
			skillSubLevel = 0;
		}
		else
		{
			skillLevel = player.getKnownSkill(_skillId).getLevel();
			skillSubLevel = SkillEnchantConverter.levelToUnderground(_skillLevel);
		}
		
		if ((_skillId <= 0) || (skillLevel <= 0) || (skillSubLevel < 0))
		{
			return;
		}
		
		player.sendPacket(new ExEnchantSkillInfoDetail(_type, _skillId, skillLevel, skillSubLevel, player));
	}
}
