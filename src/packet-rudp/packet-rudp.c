/* packet-amin.c
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2
* of the License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/

#ifdef HAVE_CONFIG_H
# include "config.h"
#endif

#include <stdio.h>
#include <glib.h>
#include <epan/packet.h>



#include <string.h>

#define PROTO_TAG_AMIN	"AMIN"

/* Wireshark ID of the AMIN protocol */
static int proto_amin = -1;



/* These are the handles of our subdissectors */
static dissector_handle_t data_handle=NULL;

static dissector_handle_t amin_handle;
void dissect_amin(tvbuff_t *tvb, packet_info *pinfo, proto_tree *tree);

static int global_amin_port = 999;

static const value_string packettypenames[] = {
	{ 0, "TEXT" },
	{ 1, "SOMETHING_ELSE" },
	{ 0, NULL }
};	


/* The following hf_* variables are used to hold the Wireshark IDs of
* our header fields; they are filled out when we call
* proto_register_field_array() in proto_register_amin()
*/
//static int hf_amin_pdu = -1;
/** Kts attempt at defining the protocol */
static gint hf_amin = -1;
static gint hf_amin_header = -1;
static gint hf_amin_length = -1;
static gint hf_amin_type = -1;
static gint hf_amin_text = -1;

/* These are the ids of the subtrees that we may be creating */
static gint ett_amin = -1;
static gint ett_amin_header = -1;
static gint ett_amin_length = -1;
static gint ett_amin_type = -1;
static gint ett_amin_text = -1;


void proto_reg_handoff_amin(void)
{
	static gboolean initialized=FALSE;

	if (!initialized) {
		data_handle = find_dissector("data");
		amin_handle = create_dissector_handle(dissect_amin, proto_amin);
		dissector_add("tcp.port", global_amin_port, amin_handle);
	}

}

void proto_register_amin (void)
{
	/* A header field is something you can search/filter on.
	* 
	* We create a structure to register our fields. It consists of an
	* array of hf_register_info structures, each of which are of the format
	* {&(field id), {name, abbrev, type, display, strings, bitmask, blurb, HFILL}}.
	*/
	static hf_register_info hf[] = {
		{ &hf_amin,
		{ "Data", "amin.data", FT_NONE, BASE_NONE, NULL, 0x0,
		"AMIN PDU", HFILL }},
		{ &hf_amin_header,
		{ "Header", "amin.header", FT_NONE, BASE_NONE, NULL, 0x0,
		 "AMIN Header", HFILL }},
		{ &hf_amin_length,
		{ "Package Length", "amin.len", FT_UINT32, BASE_DEC, NULL, 0x0,
		"Package Length", HFILL }},
		{ &hf_amin_type,
		{ "Type", "amin.type", FT_UINT8, BASE_DEC, VALS(packettypenames), 0x0,
		 "Package Type", HFILL }},
		{ &hf_amin_text,
		{ "Text", "amin.text", FT_STRING, BASE_NONE, NULL, 0x0,
		 "Text", HFILL }} 
	};
	static gint *ett[] = {
		&ett_amin,
		&ett_amin_header,
		&ett_amin_length,
		&ett_amin_type,
		&ett_amin_text
	};
	//if (proto_amin == -1) { /* execute protocol initialization only once */
	proto_amin = proto_register_protocol ("AMIN Protocol", "AMIN", "amin");

	proto_register_field_array (proto_amin, hf, array_length (hf));
	proto_register_subtree_array (ett, array_length (ett));
	register_dissector("amin", dissect_amin, proto_amin);
	//}
}
	

static void
dissect_amin(tvbuff_t *tvb, packet_info *pinfo, proto_tree *tree)
{

	proto_item *amin_item = NULL;
	proto_item *amin_sub_item = NULL;
	proto_tree *amin_tree = NULL;
	proto_tree *amin_header_tree = NULL;
	guint16 type = 0;

	if (check_col(pinfo->cinfo, COL_PROTOCOL))
		col_set_str(pinfo->cinfo, COL_PROTOCOL, PROTO_TAG_AMIN);
	/* Clear out stuff in the info column */
	if(check_col(pinfo->cinfo,COL_INFO)){
		col_clear(pinfo->cinfo,COL_INFO);
	}

	// This is not a good way of dissecting packets.  The tvb length should
	// be sanity checked so we aren't going past the actual size of the buffer.
	type = tvb_get_guint8( tvb, 4 ); // Get the type byte


	if (check_col(pinfo->cinfo, COL_INFO)) {
		col_add_fstr(pinfo->cinfo, COL_INFO, "%d > %d Info Type:[%s]",
		pinfo->srcport, pinfo->destport, 
		val_to_str(type, packettypenames, "Unknown Type:0x%02x"));
	}

	if (tree) { /* we are being asked for details */
		guint32 offset = 0;
		guint32 length = 0;

		amin_item = proto_tree_add_item(tree, proto_amin, tvb, 0, -1, FALSE);
		amin_tree = proto_item_add_subtree(amin_item, ett_amin);
		amin_header_tree = proto_item_add_subtree(amin_item, ett_amin);

		amin_sub_item = proto_tree_add_item( amin_tree, hf_amin_header, tvb, offset, -1, FALSE );
		amin_header_tree = proto_item_add_subtree(amin_sub_item, ett_amin);

		tvb_memcpy(tvb, (guint8 *)&length, offset, 4);
		proto_tree_add_uint(amin_header_tree, hf_amin_length, tvb, offset, 4, length);

		offset+=4;

		/** Type Byte */
		proto_tree_add_item(amin_header_tree, hf_amin_type, tvb, offset, 1, FALSE);
		//type = tvb_get_guint8( tvb, offset ); // Get our type byte
		offset+=1;

		if( type == 0 )
		{
			proto_tree_add_item( amin_tree, hf_amin_text, tvb, offset, length-1, FALSE );
		}
		
	}
}	
