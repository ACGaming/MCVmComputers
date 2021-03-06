package mcvmcomputers.item;

import java.util.List;

import mcvmcomputers.MainMod;
import mcvmcomputers.client.ClientMod;
import mcvmcomputers.entities.EntityPC;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ItemPCCase extends OrderableItem{
	public ItemPCCase(Settings settings) {
		super(settings, 2);
	}
	
	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		if(!world.isClient && hand == Hand.MAIN_HAND) {
			user.getStackInHand(hand).decrement(1);
			HitResult hr = user.rayTrace(10, 0f, false);
			EntityPC ek = new EntityPC(world, 
									hr.getPos().getX(),
									hr.getPos().getY(),
									hr.getPos().getZ(),
									new Vec3d(user.getPosVector().x,
												hr.getPos().getY(),
												user.getPosVector().z), user.getUuid(), user.getStackInHand(hand).getTag());
			world.spawnEntity(ek);
			MainMod.computers.put(user.getUuid(), ek);
		}
		
		if(world.isClient) {
			world.playSound(ClientMod.thePreviewEntity.getX(),
							ClientMod.thePreviewEntity.getY(),
							ClientMod.thePreviewEntity.getZ(),
							SoundEvents.BLOCK_METAL_PLACE,
							SoundCategory.BLOCKS, 1, 1, true);
		}
		
		return new TypedActionResult<ItemStack>(ActionResult.SUCCESS, user.getStackInHand(hand));
	}
	
	@Override
	public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
		if(stack.getTag() != null) {
			if (stack.getTag().contains("MoboInstalled")) {
				if(stack.getTag().getBoolean("MoboInstalled")) {
					tooltip.add(new LiteralText((stack.getTag().getBoolean("x64") ? "64-bit" : "32-bit") + " Motherboard").formatted(Formatting.GRAY));
					if(stack.getTag().getBoolean("GPUInstalled"))
						tooltip.add(new LiteralText("GPU installed").formatted(Formatting.GRAY));
					if(stack.getTag().getInt("CPUDividedBy") > 0)
						tooltip.add(new LiteralText("1/" + stack.getTag().getInt("CPUDividedBy") + " host CPU installed").formatted(Formatting.GRAY));
					if(stack.getTag().getInt("RAMSlot0") > 0)
						tooltip.add(new LiteralText(stack.getTag().getInt("RAMSlot0") + " GB of RAM in slot 1").formatted(Formatting.GRAY));
					if(stack.getTag().getInt("RAMSlot1") > 0)
						tooltip.add(new LiteralText(stack.getTag().getInt("RAMSlot1") + " GB of RAM in slot 2").formatted(Formatting.GRAY));
					if(!stack.getTag().getString("VHDName").isEmpty())
						tooltip.add(new LiteralText("Inserted hard drive: " + stack.getTag().getString("VHDName")).formatted(Formatting.GRAY));
					if(!stack.getTag().getString("ISOName").isEmpty())
						tooltip.add(new LiteralText("Inserted ISO: " + stack.getTag().getString("ISOName")).formatted(Formatting.GRAY));
				}
			}
		}
	}
	
	@Override
	public Text getName(ItemStack stack) {
		if(stack.getTag() != null) {
			if (stack.getTag().contains("MoboInstalled")) {
				if(stack.getTag().getBoolean("MoboInstalled")) {
					return new LiteralText("Built PC");
				}
			}
		}
		return new LiteralText("PC case");
	}
	
	public static ItemStack createPCStackByEntity(EntityPC pc) {
		ItemStack is = new ItemStack(ItemList.PC_CASE);
		if(pc.getMotherboardInstalled()) {
			CompoundTag ct = is.getOrCreateTag();
			ct.putBoolean("x64", pc.get64Bit());
			ct.putBoolean("MoboInstalled", pc.getMotherboardInstalled());
			ct.putBoolean("GPUInstalled", pc.getGpuInstalled());
			ct.putInt("CPUDividedBy", pc.getCpuDividedBy());
			ct.putInt("RAMSlot0", pc.getGigsOfRamInSlot0());
			ct.putInt("RAMSlot1", pc.getGigsOfRamInSlot1());
			ct.putString("VHDName", pc.getHardDriveFileName());
			ct.putString("ISOName", pc.getIsoFileName());
		}
		return is;
	}
}
