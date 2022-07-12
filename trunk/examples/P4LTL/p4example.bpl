//#LTLProperty: (match(hdr.ethernet.etherType != 2048bv16) => modify(hdr.ipv4.totalLen = old(hdr.ipv4.totalLen) - 1bv16 + 3bv16))
//#LTLFairness: <>(match(hdr.ipv4.srcAddr=127.0.0.1/24))
type Ref;
type error=bv1;
type HeaderStack = [int]Ref;

var last:[HeaderStack]Ref;
var forward:bool;
var isValid:[Ref]bool;
var emit:[Ref]bool;
var stack.index:[HeaderStack]int;
var size:[HeaderStack]int;
var drop:bool;

// Struct standard_metadata_t
var standard_metadata.ingress_port:bv9;
var standard_metadata.egress_spec:bv9;
var standard_metadata.egress_port:bv9;
var standard_metadata.instance_type:bv32;
var standard_metadata.packet_length:bv32;
var standard_metadata.enq_timestamp:bv32;
var standard_metadata.enq_qdepth:bv19;
var standard_metadata.deq_timedelta:bv32;
var standard_metadata.deq_qdepth:bv19;
var standard_metadata.ingress_global_timestamp:bv48;
var standard_metadata.egress_global_timestamp:bv48;
var standard_metadata.mcast_grp:bv16;
var standard_metadata.egress_rid:bv16;
var standard_metadata.checksum_error:bv1;
var standard_metadata.parser_error:error;
var standard_metadata.priority:bv3;

// Struct routing_metadata_t

// Struct metadata
var meta.routing_metadata.nhop_ipv4:bv32;

// Struct headers

// Header ethernet_t
var hdr.ethernet:Ref;
var hdr.ethernet.dstAddr:bv48;
var hdr.ethernet.srcAddr:bv48;
var hdr.ethernet.etherType:bv16;

// Header ipv4_t
var hdr.ipv4:Ref;
var hdr.ipv4.version:bv4;
var hdr.ipv4.ihl:bv4;
var hdr.ipv4.diffserv:bv8;
var hdr.ipv4.totalLen:bv16;
var hdr.ipv4.identification:bv16;
var hdr.ipv4.flags:bv3;
var hdr.ipv4.fragOffset:bv13;
var hdr.ipv4.ttl:bv8;
var hdr.ipv4.protocol:bv8;
var hdr.ipv4.hdrChecksum:bv16;
var hdr.ipv4.srcAddr:bv32;
var hdr.ipv4.dstAddr:bv32;

// Table send_frame_0 Actionlist Declaration
type send_frame_0.action;
const unique send_frame_0.action.rewrite_mac : send_frame_0.action;
const unique send_frame_0.action._drop : send_frame_0.action;
var send_frame_0.action_run : send_frame_0.action;
var send_frame_0.hit : bool;

function {:bvbuiltin "bvadd"} add.bv8(left:bv8, right:bv8) returns(bv8);

// Table forward_0 Actionlist Declaration
type forward_0.action;
const unique forward_0.action.set_dmac : forward_0.action;
const unique forward_0.action._drop_2 : forward_0.action;
var forward_0.action_run : forward_0.action;
var forward_0.hit : bool;

// Table ipv4_lpm_0 Actionlist Declaration
type ipv4_lpm_0.action;
const unique ipv4_lpm_0.action.set_nhop : ipv4_lpm_0.action;
const unique ipv4_lpm_0.action._drop_4 : ipv4_lpm_0.action;
var ipv4_lpm_0.action_run : ipv4_lpm_0.action;
var ipv4_lpm_0.hit : bool;

function {:bvbuiltin "bvugt"} bugt.bv8(left:bv8, right:bv8) returns(bool);

// Action NoAction_0
procedure {:inline 1} NoAction_0()
{
}

// Action NoAction_1
procedure {:inline 1} NoAction_1()
{
}

// Action NoAction_5
procedure {:inline 1} NoAction_5()
{
}

// Parser ParserImpl
procedure {:inline 1} ParserImpl()
	modifies drop, isValid;
{
    call start();
}

// Action _drop
procedure {:inline 1} _drop()
	modifies drop;
{
    call mark_to_drop();
}

// Action _drop_2
procedure {:inline 1} _drop_2()
	modifies drop;
{
    call mark_to_drop();
}

// Action _drop_4
procedure {:inline 1} _drop_4()
	modifies drop;
{
    call mark_to_drop();
}
procedure {:inline 1} accept()
{
}
procedure clear_drop();
    ensures drop==false;
	modifies drop;
procedure clear_emit();
    ensures (forall header:Ref:: emit[header]==false);
	modifies emit;
procedure clear_forward();
    ensures forward==false;
	modifies forward;
procedure clear_valid();
    ensures (forall header:Ref:: isValid[header]==false);
	modifies isValid;

// Control computeChecksum
procedure {:inline 1} computeChecksum()
{
    // update_checksum
}

// Control egress
procedure {:inline 1} egress()
	modifies drop, hdr.ethernet.srcAddr, standard_metadata.egress_port;
{
    call send_frame_0.apply();
}

procedure forward_0.action_run.limit();
    ensures(forward_0.action_run==forward_0.action.set_dmac || forward_0.action_run==forward_0.action._drop_2);
	modifies forward_0.action_run;

// Table forward_0
procedure {:inline 1} forward_0.apply()
	modifies drop, hdr.ethernet.dstAddr, meta.routing_metadata.nhop_ipv4;
{
    var set_dmac.dmac:bv48;
    meta.routing_metadata.nhop_ipv4 := meta.routing_metadata.nhop_ipv4;

    call forward_0.apply_table_entry();

    action_set_dmac:
    if(forward_0.action_run == forward_0.action.set_dmac){
        call set_dmac(set_dmac.dmac);
    }

    action__drop_2:
    if(forward_0.action_run == forward_0.action._drop_2){
        call _drop_2();
    }
}

// Table Entry forward_0.apply_table_entry
procedure forward_0.apply_table_entry();

// Table Exit forward_0.apply_table_exit
procedure forward_0.apply_table_exit();
procedure {:inline 1} havocProcedure()
	modifies hdr.ethernet.dstAddr, hdr.ethernet.etherType, hdr.ethernet.srcAddr, hdr.ipv4.diffserv, hdr.ipv4.dstAddr, hdr.ipv4.flags, hdr.ipv4.fragOffset, hdr.ipv4.hdrChecksum, hdr.ipv4.identification, hdr.ipv4.ihl, hdr.ipv4.protocol, hdr.ipv4.srcAddr, hdr.ipv4.totalLen, hdr.ipv4.ttl, hdr.ipv4.version, isValid;
{
    isValid[hdr.ethernet] := false;
    havoc hdr.ethernet.dstAddr;
    havoc hdr.ethernet.srcAddr;
    havoc hdr.ethernet.etherType;
    isValid[hdr.ipv4] := false;
    havoc hdr.ipv4.version;
    havoc hdr.ipv4.ihl;
    havoc hdr.ipv4.diffserv;
    havoc hdr.ipv4.totalLen;
    havoc hdr.ipv4.identification;
    havoc hdr.ipv4.flags;
    havoc hdr.ipv4.fragOffset;
    havoc hdr.ipv4.ttl;
    havoc hdr.ipv4.protocol;
    havoc hdr.ipv4.hdrChecksum;
    havoc hdr.ipv4.srcAddr;
    havoc hdr.ipv4.dstAddr;
}

// Control ingress
procedure {:inline 1} ingress()
	modifies drop, forward, hdr.ethernet.dstAddr, hdr.ipv4.dstAddr, hdr.ipv4.ttl, meta.routing_metadata.nhop_ipv4, standard_metadata.egress_spec;
{
    if((isValid[hdr.ipv4]) && (bugt.bv8(hdr.ipv4.ttl, 0bv8))){
        call ipv4_lpm_0.apply();
        call forward_0.apply();
    }
}
procedure init.stack.index();
//    ensures (forall s:HeaderStack::stack.index[s]==0);
	modifies stack.index;

procedure ipv4_lpm_0.action_run.limit();
    ensures(ipv4_lpm_0.action_run==ipv4_lpm_0.action.set_nhop || ipv4_lpm_0.action_run==ipv4_lpm_0.action._drop_4);
	modifies ipv4_lpm_0.action_run;

// Table ipv4_lpm_0
procedure {:inline 1} ipv4_lpm_0.apply()
	modifies drop, forward, hdr.ipv4.dstAddr, hdr.ipv4.ttl, meta.routing_metadata.nhop_ipv4, standard_metadata.egress_spec;
{
    var set_nhop.port:bv9;
    var set_nhop.nhop_ipv4:bv32;
    hdr.ipv4.dstAddr := hdr.ipv4.dstAddr;

    call ipv4_lpm_0.apply_table_entry();

    action_set_nhop:
    if(ipv4_lpm_0.action_run == ipv4_lpm_0.action.set_nhop){
        call set_nhop(set_nhop.nhop_ipv4, set_nhop.port);
    }

    action__drop_4:
    if(ipv4_lpm_0.action_run == ipv4_lpm_0.action._drop_4){
        call _drop_4();
    }
}

// Table Entry ipv4_lpm_0.apply_table_entry
procedure ipv4_lpm_0.apply_table_entry();

// Table Exit ipv4_lpm_0.apply_table_exit
procedure ipv4_lpm_0.apply_table_exit();
procedure {:inline 1} main()
	modifies drop, forward, hdr.ethernet.dstAddr, hdr.ethernet.etherType, hdr.ethernet.srcAddr, hdr.ipv4.diffserv, hdr.ipv4.dstAddr, hdr.ipv4.flags, hdr.ipv4.fragOffset, hdr.ipv4.hdrChecksum, hdr.ipv4.identification, hdr.ipv4.ihl, hdr.ipv4.protocol, hdr.ipv4.srcAddr, hdr.ipv4.totalLen, hdr.ipv4.ttl, hdr.ipv4.version, isValid, meta.routing_metadata.nhop_ipv4, standard_metadata.egress_port, standard_metadata.egress_spec;
{
        drop := false;
        forward := false;
        call havocProcedure();
        //assume(hdr.ethernet.etherType != 2048bv16);
        call ParserImpl();
        call verifyChecksum();
        call ingress();
        call egress();
        call computeChecksum();
        if(forward == false){
            drop := true;
        }
        // assert((hdr.ethernet.etherType != 2048bv16) ==> (drop == true));
}
procedure mainProcedure()
	modifies drop, emit, forward, forward_0.action_run, hdr.ethernet.dstAddr, hdr.ethernet.etherType, hdr.ethernet.srcAddr, hdr.ipv4.diffserv, hdr.ipv4.dstAddr, hdr.ipv4.flags, hdr.ipv4.fragOffset, hdr.ipv4.hdrChecksum, hdr.ipv4.identification, hdr.ipv4.ihl, hdr.ipv4.protocol, hdr.ipv4.srcAddr, hdr.ipv4.totalLen, hdr.ipv4.ttl, hdr.ipv4.version, ipv4_lpm_0.action_run, isValid, meta.routing_metadata.nhop_ipv4, send_frame_0.action_run, stack.index, standard_metadata.egress_port, standard_metadata.egress_spec;
{
    assume(hdr.ethernet != hdr.ipv4);
    call ipv4_lpm_0.action_run.limit();
    call forward_0.action_run.limit();
    call send_frame_0.action_run.limit();
    call clear_forward();
    call clear_drop();
    call clear_valid();
    call clear_emit();
    call init.stack.index();
	while (true){
		call main();
	}
}
procedure mark_to_drop();
    ensures drop==true;
	modifies drop;
procedure {:inline 1} packet_in.extract(header:Ref)
modifies isValid;
{
    isValid[header] := true;
}

//Parser State parse_ethernet
procedure {:inline 1} parse_ethernet()
	modifies isValid;
{
    isValid[hdr.ethernet] := true;
    if(hdr.ethernet.etherType == 2048bv16){
        call parse_ipv4();
    }
}

//Parser State parse_ipv4
procedure {:inline 1} parse_ipv4()
	modifies isValid;
{
    call packet_in.extract(hdr.ipv4);
    call accept();
}
procedure reject();
    ensures drop==true;
	modifies drop;

// Action rewrite_mac
procedure {:inline 1} rewrite_mac(smac:bv48)
	modifies hdr.ethernet.srcAddr;
{
    hdr.ethernet.srcAddr := smac;
}

procedure send_frame_0.action_run.limit();
    ensures(send_frame_0.action_run==send_frame_0.action.rewrite_mac || send_frame_0.action_run==send_frame_0.action._drop);
	modifies send_frame_0.action_run;

// Table send_frame_0
procedure {:inline 1} send_frame_0.apply()
	modifies drop, hdr.ethernet.srcAddr, standard_metadata.egress_port;
{
    var rewrite_mac.smac:bv48;
    standard_metadata.egress_port := standard_metadata.egress_port;

    call send_frame_0.apply_table_entry();

    action_rewrite_mac:
    if(send_frame_0.action_run == send_frame_0.action.rewrite_mac){
        call rewrite_mac(rewrite_mac.smac);
    }

    action__drop:
    if(send_frame_0.action_run == send_frame_0.action._drop){
        call _drop();
    }
}

// Table Entry send_frame_0.apply_table_entry
procedure send_frame_0.apply_table_entry();

// Table Exit send_frame_0.apply_table_exit
procedure send_frame_0.apply_table_exit();
procedure {:inline 1} setInvalid(header:Ref);
    ensures (isValid[header] == false);
	modifies isValid;
procedure {:inline 1} setValid(header:Ref);

// Action set_dmac
procedure {:inline 1} set_dmac(dmac:bv48)
	modifies hdr.ethernet.dstAddr;
{
    hdr.ethernet.dstAddr := dmac;
}

// Action set_nhop
procedure {:inline 1} set_nhop(nhop_ipv4:bv32, port:bv9)
	modifies forward, hdr.ipv4.ttl, meta.routing_metadata.nhop_ipv4, standard_metadata.egress_spec;
{
    meta.routing_metadata.nhop_ipv4 := nhop_ipv4;
    standard_metadata.egress_spec := port;
    forward := true;
    hdr.ipv4.ttl := add.bv8(hdr.ipv4.ttl, 255bv8);
}

//Parser State start
procedure {:inline 1} start()
	modifies isValid;
{
    call parse_ethernet();
}

// Control verifyChecksum
procedure {:inline 1} verifyChecksum()
{
    // verify_checksum
}
