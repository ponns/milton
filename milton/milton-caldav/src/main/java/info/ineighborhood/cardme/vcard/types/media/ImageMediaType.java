/*
 * Copyright (C) 2012 McEvoy Software Ltd
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package info.ineighborhood.cardme.vcard.types.media;

/**
 * Copyright (c) 2004, Neighborhood Technologies
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * Neither the name of Neighborhood Technologies nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

/**
 * 
 * @author George El-Haddad
 * <br/>
 * Mar 10, 2010
 *
 */
public enum ImageMediaType {

	/*
	 * IANA Registered Media Types.
	 * Some may not have registered an extension.
	 */
	
	CGM("CGM", "image/cgm", "cgm"),
	JP2("JP2", "image/jp2", "jp2"),
	JPM("JPM", "image/jpm", "jpm"),
	JPX("JPX", "image/jpx", "jpf"),
	NAPLPS("NAPLPS", "image/naplps", ""),
	PNG("PNG", "image/png", "png"),
	BTIF("BTIF", ".image/prs.btif", "btif"),
	PTI("PTI", "image/prs.pti", "pti"),
	DJVU("DJVU", "image/vnd.djvu", "djvu"),
	SVF("SVF", "image/vnd.svf", "svf"),
	WBMP("WBMP", "image/vnd.wap.wbmp", "wbmp"),
	PSD("PSD", "image/vnd.adobe.photoshop", "psd"),
	INF2("INF2", "image/vnd.cns.inf2", ""),
	DWG("DWG", "image/vnd.dwg", "dwg"),
	DXF("DXF", "image/vnd.dxf", "dxf"),
	FBS("FBS", "image/vnd.fastbidsheet", "fbs"),
	FPX("FPX", "image/vnd.fpx", "fpx"),
	FST("FST", "image/vnd.fst", "fst"),
	MMR("MMR", "image/vnd.fujixerox.edmics-mmr", "mmr"),
	RLC("RLC", "image/vnd.fujixerox.edmics-rlc", "rlc"),
	PGB("PGB", "image/vnd.globalgraphics.pgb", "pgb"),
	ICO("ICO", "image/vnd.microsoft.icon", "ico"),
	MIX("MIX", "image/vnd.mix", ""),
	MDI("MDI", "image/vnd.ms-modi", "mdi"),
	PIC("PIC", "image/vnd.radiance", "pic"),			//pic, hdr, rgbe, xyze
	SPNG("SPNG", "image/vnd.sealed.png", "spng"),			//spng, spn, s1n
	SGIF("SGIF", "image/vnd.sealedmedia.softseal.gif", "sgif"),	//sgif, sgi, s1g
	SJPG("SJPG", "image/vnd.sealedmedia.softseal.jpg", "sjpg"),	//sjpg, sjp, s1j
	XIF("XIF", "image/vnd.xiff", "xif"),
	JPEG("JPEG", "image/jpeg", "jpg"),
	NON_STANDARD("NON_STANDARD","","");
	
	private String typeName;
	private String ianaRegisteredName;
	private String extension;
	ImageMediaType(String _typeName, String _ianaRegisteredName, String _extension) {
		typeName = _typeName;
		ianaRegisteredName = _ianaRegisteredName;
		extension = _extension;
	}
	
	public String getTypeName()
	{
		return typeName;
	}
	
	public String getIanaRegisteredName()
	{
		return ianaRegisteredName;
	}
	
	public String getExtension()
	{
		return extension;
	}
	
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	
	public void setIanaRegisteredName(String ianaRegisteredName) {
		this.ianaRegisteredName = ianaRegisteredName;
	}
	
	public void setExtension(String extension) {
		this.extension = extension;
	}
	
	@Override
	public String toString()
	{

		return typeName;
	}
}
