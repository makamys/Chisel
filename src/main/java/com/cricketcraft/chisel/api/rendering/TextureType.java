package com.cricketcraft.chisel.api.rendering;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import com.cricketcraft.chisel.api.carving.CarvableHelper;
import com.cricketcraft.chisel.api.carving.ICarvingVariation;
import com.cricketcraft.chisel.client.render.RenderBlocksColumn;

/**
 * Handles all default {@link ISubmapManager} behavior
 */
@SuppressWarnings("unchecked")
public enum TextureType {

	// @formatter:off
	TOPSIDE("top", "side") {
		@Override
		protected Object registerIcons(ICarvingVariation variation, String modName, String texturePath, IIconRegister register) {
			return new IIcon[]{
					register.registerIcon(modName + ":" + texturePath + "-side"),
					register.registerIcon(modName + ":" + texturePath + "-top")
			};
		}
		
		@Override
		protected IIcon getIcon(ICarvingVariation variation, Object cachedObject, int side, int meta) {
			IIcon[] icons = (IIcon[]) cachedObject;
			return side > 1 ? icons[0] : icons[1];
		}
	},
	TOPBOTSIDE("top", "bottom", "side"){
		@Override
		protected Object registerIcons(ICarvingVariation variation, String modName, String texturePath, IIconRegister register) {
			return new IIcon[]{
					register.registerIcon(modName + ":" + texturePath + "-side"),
					register.registerIcon(modName + ":" + texturePath + "-bottom"),
					register.registerIcon(modName + ":" + texturePath + "-top")
			};
		}
		
		@Override
		protected IIcon getIcon(ICarvingVariation variation, Object cachedObject, int side, int meta) {
			IIcon[] icons = (IIcon[]) cachedObject;
			return side > 1 ? icons[0] : icons[side + 1];
		}
	},
	CTMV("ctmv", "top"){
		@Override
		protected Object registerIcons(ICarvingVariation variation, String modName, String texturePath, IIconRegister register) {
			return Pair.of(new TextureSubmap(register.registerIcon(modName + ":" + texturePath + "-ctmv"), 2, 2), register.registerIcon(modName + ":" + texturePath + "-top"));
		}
		
		@Override
		protected IIcon getIcon(ICarvingVariation variation, Object cachedObject, int side, int meta) {
			Pair<TextureSubmap, IIcon> data = (Pair<TextureSubmap, IIcon>) cachedObject;
			return side < 2 ? data.getRight() : data.getLeft().getSubIcon(0, 0);
		}
		
		@Override
		protected IIcon getIcon(ICarvingVariation variation, Object cachedObject, IBlockAccess world, int x, int y, int z, int side, int meta) {
			Pair<IIcon, TextureSubmap> data = (Pair<IIcon, TextureSubmap>) cachedObject;
			if (side < 2)
				return data.getLeft();

			Block block = world.getBlock(x, y, z);
			boolean topConnected = ctm.isConnected(world, x, y + 1, z, side, block, meta);
			boolean botConnected = ctm.isConnected(world, x, y - 1, z, side, block, meta);

			TextureSubmap map = data.getRight();
			if (topConnected && botConnected)
				return map.getSubIcon(0, 1);
			if (topConnected && !botConnected)
				return map.getSubIcon(1, 1);
			if (!topConnected && botConnected)
				return map.getSubIcon(1, 0);
			return map.getSubIcon(0, 0);
		}
		
		@Override
		protected RenderBlocks createRenderContext(RenderBlocks rendererOld, IBlockAccess world, Object cachedObject) {
			RenderBlocksColumn ret = theRenderBlocksColumn;
			Pair<IIcon, TextureSubmap> data = (Pair<IIcon, TextureSubmap>) cachedObject;
			
			ret.blockAccess = world;
			ret.renderMaxX = 1.0;
			ret.renderMaxY = 1.0;
			ret.renderMaxZ = 1.0;

			ret.submap = data.getRight();
			ret.iconTop = data.getLeft();
			return ret;
		}
	},
	CTMH("ctmh", "top"){
		@Override
		protected Object registerIcons(ICarvingVariation variation, String modName, String texturePath, IIconRegister register) {
			return Pair.of(new TextureSubmap(register.registerIcon(modName + ":" + texturePath + "-ctmh"), 2, 2), register.registerIcon(modName + ":" + texturePath + "-top"));
		}		
		
		@Override
		protected IIcon getIcon(ICarvingVariation variation, Object cachedObject, int side, int meta) {
			return CTMV.getIcon(variation, cachedObject, side, meta);
		}
		
		@Override
		protected IIcon getIcon(ICarvingVariation variation, Object cachedObject, IBlockAccess world, int x, int y, int z, int side, int meta) {
			Pair<IIcon, TextureSubmap> data = (Pair<IIcon, TextureSubmap>) cachedObject;
			if (side < 2)
				return data.getLeft();

			Block block = ctm.getBlockOrFacade(world, x, y, z, side);

			boolean p;
			boolean n;
			boolean reverse = side == 2 || side == 5;

			if (side < 4) {
				p = ctm.isConnected(world, x - 1, y, z, side, block, meta);
				n = ctm.isConnected(world, x + 1, y, z, side, block, meta);
			} else {
				p = ctm.isConnected(world, x, y, z - 1, side, block, meta);
				n = ctm.isConnected(world, x, y, z + 1, side, block, meta);
			}
			
			TextureSubmap map = data.getRight();
			if (p && n)
				return map.getSubIcon(1, 0);
			else if (p)
				return map.getSubIcon(reverse ? 0 : 1, 1);
			else if (n)
				return map.getSubIcon(reverse ? 0 : 1, 1);
			return map.getSubIcon(0, 0);
		}
	},
	V9("v9"){
		@Override
		protected Object registerIcons(ICarvingVariation variation, String modName, String texturePath, IIconRegister register) {
			return new TextureSubmap(register.registerIcon(modName + ":" + texturePath + "-v9"), 3, 3);
		}
		
		@Override
		protected IIcon getIcon(ICarvingVariation variation, Object cachedObject, int side, int meta) {
			return ((TextureSubmap)cachedObject).getSubIcon(1, 1);
		}

		@Override
		protected IIcon getIcon(ICarvingVariation variation, Object cachedObject, IBlockAccess world, int x, int y, int z, int side, int meta) {           
			int variationSize = (this == TextureType.V9) ? 3 : 2;
			TextureSubmap map = (TextureSubmap) cachedObject;

			int xModulus = x % variationSize;
			int zModulus = z % variationSize;
			//This ensures that blocks placed near 0,0 or it's axis' do not misbehave
			int textureX = (xModulus < 0) ? (xModulus + variationSize) : xModulus;
			int textureZ = (zModulus < 0) ? (zModulus + variationSize) : zModulus;
			//Always invert the y index
			int textureY = (variationSize - (y % variationSize) - 1);

			if (side == 2 || side == 5) {
				//For WEST, SOUTH reverse the indexes for both X and Z
				textureX = (variationSize - textureX - 1);
				textureZ = (variationSize - textureZ - 1);
			} /*else if (side == 0) {
            //For DOWN, reverse the indexes for only Z
            textureZ = (variationSize - textureZ - 1);
        	}*/

			int index;
			if (side == 0 || side == 1) {
				// DOWN || UP
				index = textureX + textureZ * variationSize;
			} else if (side == 2 || side == 3) {
				// NORTH || SOUTH
				index = textureX + textureY * variationSize;
			} else {
				// WEST || EAST
				index = textureZ + textureY * variationSize;
			}

			return map.getSubIcon(index % variationSize, index / variationSize);
		}
	},
	V4("v4"){
		@Override
		protected Object registerIcons(ICarvingVariation variation, String modName, String texturePath, IIconRegister register) {
			return new TextureSubmap(register.registerIcon(modName + ":" + texturePath + "-v4"), 3, 3);
		}
		
		@Override
		protected IIcon getIcon(ICarvingVariation variation, Object cachedObject, int side, int meta) {
			return ((TextureSubmap)cachedObject).getSubIcon(0, 0);
		}
		
		@Override
		protected IIcon getIcon(ICarvingVariation variation, Object cachedObject, IBlockAccess world, int x, int y, int z, int side, int meta) {
			return V9.getIcon(variation, cachedObject, world, x, y, z, side, meta);
		}
	},
	CTMX("", "ctm"){
		@Override
		protected Object registerIcons(ICarvingVariation variation, String modName, String texturePath, IIconRegister register) {
			IIcon baseIcon = register.registerIcon(modName + ":" + texturePath);
			return Triple.of(
					baseIcon,
					new TextureSubmap(register.registerIcon(modName + ":" + texturePath + "-ctm"), 4, 4),
					new TextureSubmap(baseIcon, 2, 2)
			);
		}
		
		@Override
		protected IIcon getIcon(ICarvingVariation variation, Object cachedObject, int side, int meta) {
			return ((Triple<IIcon, ?, ?>)cachedObject).getLeft();
		}
		
		@Override
		protected RenderBlocks createRenderContext(RenderBlocks rendererOld, IBlockAccess world, Object cachedObject) {
			RenderBlocksCTM ret = theRenderBlocksCTM;
			Triple<?, TextureSubmap, TextureSubmap> data = (Triple<?, TextureSubmap, TextureSubmap>) cachedObject;
			ret.blockAccess = world;
			ret.renderMaxX = 1.0;
			ret.renderMaxY = 1.0;
			ret.renderMaxZ = 1.0;

			ret.submap = data.getMiddle();
			ret.submapSmall = data.getRight();

			ret.rendererOld = rendererOld;
			return ret;
		}
	},
	R16("r16"){
		@Override
		protected Object registerIcons(ICarvingVariation variation, String modName, String texturePath, IIconRegister register) {
			return new TextureSubmap(register.registerIcon(modName + ":" + texturePath + "-r16"), 4, 4);
		}		
		
		@Override
		protected IIcon getIcon(ICarvingVariation variation, Object cachedObject, int side, int meta) {
			return V9.getIcon(variation, cachedObject, side, meta);
		}
	},
	R9("r9"){
		@Override
		protected Object registerIcons(ICarvingVariation variation, String modName, String texturePath, IIconRegister register) {
			return new TextureSubmap(register.registerIcon(modName + ":" + texturePath + "-r9"), 3, 3);
		}	
		
		@Override
		protected IIcon getIcon(ICarvingVariation variation, Object cachedObject, int side, int meta) {
			return V9.getIcon(variation, cachedObject, side, meta);
		}
		
		@Override
		protected IIcon getIcon(ICarvingVariation variation, Object cachedObject, IBlockAccess world, int x, int y, int z, int side, int meta) {
			return R4.getIcon(variation, cachedObject, side, meta);
		}
	},
	R4("r4"){
		
		@Override
		protected Object registerIcons(ICarvingVariation variation, String modName, String texturePath, IIconRegister register) {
			return new TextureSubmap(register.registerIcon(modName + ":" + texturePath + "-r4"), 2, 2);
		}	
		
		@Override
		protected IIcon getIcon(ICarvingVariation variation, Object cachedObject, int side, int meta) {
			return V4.getIcon(variation, cachedObject, side, meta);
		}
		
		@Override
		protected IIcon getIcon(ICarvingVariation variation, Object cachedObject, IBlockAccess world, int x, int y, int z, int side, int meta) {
			rand.setSeed(x + y + z + side);
			rand.nextBoolean();
			int size = this == TextureType.R4 ? 2 : 3;
			return ((TextureSubmap)cachedObject).getSubIcon(rand.nextInt(size), rand.nextInt(size));
		}
	},
	NORMAL,
	CUSTOM;
	
	private static final TextureType[] VALUES;
	private static final CTM ctm = CTM.getInstance();
	private static final Random rand = new Random();
	private static final RenderBlocksCTM theRenderBlocksCTM = new RenderBlocksCTM();
	private static final RenderBlocksColumn theRenderBlocksColumn = new RenderBlocksColumn();

	private String[] suffixes;
	static {
		VALUES = ArrayUtils.subarray(values(), 0, values().length - 1);
	}

	private TextureType(String... suffixes) {
		this.suffixes = suffixes.length == 0 ? new String[] { "" } : suffixes;
	}
	
	public ISubmapManager<RenderBlocks> createManagerFor(ICarvingVariation variation, String texturePath) {
		return new SubmapManagerDefault(this, variation, texturePath);
	}
	
	public ISubmapManager<RenderBlocks> createManagerFor(ICarvingVariation variation, Block block, int meta) {
		return new SubmapManagerExistingIcon(this, variation, block, meta);
	}
	
	protected Object registerIcons(ICarvingVariation variation, String modName, String texturePath, IIconRegister register) {
		return register.registerIcon(modName + ":" + texturePath);
	}
	
	protected IIcon getIcon(ICarvingVariation variation, Object cachedObject, int side, int meta) {
		return (IIcon) cachedObject;
	}
	
	protected IIcon getIcon(ICarvingVariation variation, Object cachedObject, IBlockAccess world, int x, int y, int z, int side, int meta) {
		return getIcon(variation, cachedObject, side, meta);
	}

	protected RenderBlocks createRenderContext(RenderBlocks rendererOld, IBlockAccess world, Object cachedObject) {
		return null;
	}

	public static TextureType getTypeFor(CarvableHelper inst, String modid, String path) {
		if (path == null) {
			return CUSTOM;
		}
		for (TextureType t : VALUES) {
			boolean matches = true;
			for (String s : t.suffixes) {
				if (!exists(modid, path, s.isEmpty() ? s : "-" + s)) {
					matches = false;
				}
			}
			if (matches) {
				return t;
			}
		}
		return CUSTOM;
	}
	
	// This is ugly, but faster than class.getResource
	private static boolean exists(String modid, String path, String postfix) {
		ResourceLocation rl = new ResourceLocation(modid, "textures/blocks/" + path + postfix + ".png");
		try {
			Minecraft.getMinecraft().getResourceManager().getAllResources(rl);
			return true;
		} catch (Throwable t) {
			return false;
		}
	}
	
	private abstract static class SubmapManagerBase<T extends RenderBlocks> implements ISubmapManager<T> {
		protected final TextureType type;
		protected ICarvingVariation variation;
		protected Object cachedObject;
		
		private SubmapManagerBase(TextureType type, ICarvingVariation variation) {
			this.type = type;
			this.variation = variation;
		}
		
		@Override
		public IIcon getIcon(int side, int meta) {
			return type.getIcon(variation, cachedObject, side, meta);
		}

		@Override
		public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
			return type.getIcon(variation, cachedObject, world, x, y, z, side, world.getBlockMetadata(x, y, z));
		}
		
		@Override
		public T createRenderContext(RenderBlocks rendererOld, IBlockAccess world) {
			return (T) type.createRenderContext(rendererOld, world, cachedObject);
		}
		
		@Override
		public void preRenderSide(RenderBlocks renderer, IBlockAccess world, int x, int y, int z, ForgeDirection side) {
		}
		
		@Override
		public void postRenderSide(RenderBlocks renderer, IBlockAccess world, int x, int y, int z, ForgeDirection side) {			
		}
	}

	private static class SubmapManagerDefault extends SubmapManagerBase<RenderBlocks> {

		private String texturePath;
		
		private SubmapManagerDefault(TextureType type, ICarvingVariation variation, String texturePath) {
			super(type, variation);
			this.texturePath = texturePath;
		}

		@Override
		public void registerIcons(String modName, Block block, IIconRegister register) {
			cachedObject = type.registerIcons(variation, modName, texturePath, register);
		}
	}
	
	private static class SubmapManagerExistingIcon extends SubmapManagerBase<RenderBlocks> {
		
		private Block block;
		private int meta;
		
		private SubmapManagerExistingIcon(TextureType type, ICarvingVariation variation, Block block, int meta) {
			super(type, variation);
			this.block = block;
			this.meta = meta;
		}
		
		@Override
		public IIcon getIcon(int side, int meta) {
			return block.getIcon(side, this.meta);
		}
		
		@Override
		public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
			return getIcon(side, world.getBlockMetadata(x, y, z));
		}

		@Override
		public void registerIcons(String modName, Block block, IIconRegister register) {
		}
	}
}