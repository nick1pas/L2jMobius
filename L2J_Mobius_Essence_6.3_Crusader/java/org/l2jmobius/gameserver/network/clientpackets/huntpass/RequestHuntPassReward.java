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
package org.l2jmobius.gameserver.network.clientpackets.huntpass;

import org.l2jmobius.commons.network.ReadablePacket;
import org.l2jmobius.commons.threads.ThreadPool;
import org.l2jmobius.gameserver.data.ItemTable;
import org.l2jmobius.gameserver.data.xml.HuntPassData;
import org.l2jmobius.gameserver.model.HuntPass;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.request.RewardRequest;
import org.l2jmobius.gameserver.model.holders.ItemHolder;
import org.l2jmobius.gameserver.model.item.ItemTemplate;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.clientpackets.ClientPacket;
import org.l2jmobius.gameserver.network.serverpackets.SystemMessage;
import org.l2jmobius.gameserver.network.serverpackets.huntpass.HuntPassInfo;
import org.l2jmobius.gameserver.network.serverpackets.huntpass.HuntPassSayhasSupportInfo;
import org.l2jmobius.gameserver.network.serverpackets.huntpass.HuntPassSimpleInfo;

/**
 * @author Serenitty
 */
public class RequestHuntPassReward implements ClientPacket
{
	private int _huntPassType;
	
	@Override
	public void read(ReadablePacket packet)
	{
		_huntPassType = packet.readByte();
		packet.readByte(); // is Premium?
	}
	
	@Override
	public void run(GameClient client)
	{
		final Player player = client.getPlayer();
		if (player == null)
		{
			return;
		}
		
		if (player.hasRequest(RewardRequest.class))
		{
			return;
		}
		player.addRequest(new RewardRequest(player));
		
		final HuntPass huntPass = player.getHuntPass();
		final int rewardIndex = huntPass.getRewardStep();
		if (rewardIndex >= HuntPassData.getInstance().getRewardsCount())
		{
			return;
		}
		
		final ItemHolder reward = HuntPassData.getInstance().getRewards().get(rewardIndex);
		final ItemTemplate itemTemplate = ItemTable.getInstance().getTemplate(reward.getId());
		final long weight = itemTemplate.getWeight() * reward.getCount();
		final long slots = itemTemplate.isStackable() ? 1 : reward.getCount();
		if (!player.getInventory().validateWeight(weight) || !player.getInventory().validateCapacity(slots))
		{
			player.sendPacket(SystemMessageId.YOUR_INVENTORY_S_WEIGHT_SLOT_LIMIT_HAS_BEEN_EXCEEDED_SO_YOU_CAN_T_RECEIVE_THE_REWARD_PLEASE_FREE_UP_SOME_SPACE_AND_TRY_AGAIN);
			return;
		}
		
		updateSayhaTime(player);
		premiumReward(player);
		huntPass.setRewardStep(rewardIndex + 1);
		huntPass.setRewardAlert(false);
		player.sendPacket(new HuntPassInfo(player, _huntPassType));
		player.sendPacket(new HuntPassSayhasSupportInfo(player));
		player.sendPacket(new HuntPassSimpleInfo(player));
		
		ThreadPool.schedule(() -> player.removeRequest(RewardRequest.class), 50);
	}
	
	private void updateSayhaTime(Player player)
	{
		final HuntPass huntpass = player.getHuntPass();
		final int rewardIndex = huntpass.getRewardStep();
		if (rewardIndex >= HuntPassData.getInstance().getRewardsCount())
		{
			return;
		}
		
		if (huntpass.isPremium() && ((huntpass.getPremiumRewardStep() < rewardIndex) || (huntpass.getPremiumRewardStep() >= HuntPassData.getInstance().getPremiumRewardsCount())))
		{
			return;
		}
		
		final ItemHolder reward = HuntPassData.getInstance().getRewards().get(rewardIndex);
		if (reward.getId() == 72286) // Sayha's Grace Sustention Points
		{
			final int count = (int) reward.getCount();
			huntpass.addSayhaTime(count);
			
			final SystemMessage msg = new SystemMessage(SystemMessageId.YOU_VE_GOT_S1_SAYHA_S_GRACE_SUSTENTION_POINT_S);
			msg.addInt(count);
			player.sendPacket(msg);
		}
		else
		{
			player.addItem("HuntPassReward", reward, player, true);
		}
	}
	
	private void premiumReward(Player player)
	{
		final HuntPass huntPass = player.getHuntPass();
		final int rewardIndex = huntPass.getPremiumRewardStep();
		if (rewardIndex >= HuntPassData.getInstance().getPremiumRewardsCount())
		{
			return;
		}
		
		final ItemHolder premiumReward = HuntPassData.getInstance().getPremiumRewards().get(rewardIndex);
		if (huntPass.isPremium())
		{
			player.addItem("SeasonPassReward", premiumReward, player, true);
			huntPass.setPremiumRewardStep(rewardIndex + 1);
		}
	}
}
