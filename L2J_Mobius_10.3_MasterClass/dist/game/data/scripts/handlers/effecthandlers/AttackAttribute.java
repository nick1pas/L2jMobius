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
package handlers.effecthandlers;

import java.util.HashSet;
import java.util.Set;

import org.l2jmobius.gameserver.enums.AttributeType;
import org.l2jmobius.gameserver.model.StatSet;
import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.effects.AbstractEffect;
import org.l2jmobius.gameserver.model.skill.Skill;
import org.l2jmobius.gameserver.model.stats.Stat;

/**
 * @author Sdw
 */
public class AttackAttribute extends AbstractEffect
{
	private final Set<AttributeType> _attributes = new HashSet<>();
	private final double _amount;
	
	public AttackAttribute(StatSet params)
	{
		_amount = params.getDouble("amount", 0);
		final String attributes = params.getString("attribute", "FIRE");
		if (attributes.contains(","))
		{
			for (String attribute : attributes.split(","))
			{
				_attributes.add(AttributeType.findByName(attribute.trim()));
			}
		}
		else
		{
			_attributes.add(AttributeType.findByName(attributes));
		}
	}
	
	@Override
	public void pump(Creature effected, Skill skill)
	{
		for (AttributeType attribute : _attributes)
		{
			switch (attribute)
			{
				case WATER:
				{
					effected.getStat().mergeAdd(Stat.WATER_POWER, _amount);
					break;
				}
				case WIND:
				{
					effected.getStat().mergeAdd(Stat.WIND_POWER, _amount);
					break;
				}
				case EARTH:
				{
					effected.getStat().mergeAdd(Stat.EARTH_POWER, _amount);
					break;
				}
				case HOLY:
				{
					effected.getStat().mergeAdd(Stat.HOLY_POWER, _amount);
					break;
				}
				case DARK:
				{
					effected.getStat().mergeAdd(Stat.DARK_POWER, _amount);
					break;
				}
			}
		}
	}
}
