package xdi2.core.xri3.parser.full.apg;

import com.coasttocoastresearch.apg.Grammar;
import java.io.PrintStream;

public class XDI3Grammar extends Grammar{

    // public API
    public static Grammar getInstance(){
        if(factoryInstance == null){
            factoryInstance = new XDI3Grammar(getRules(), getUdts(), getOpcodes());
        }
        return factoryInstance;
    }

    // rule name enum
    public static int ruleCount = 72;
    public enum RuleNames{
        ABSOLUTE("absolute", 5, 26, 3),
        ABSOLUTE_INVERSE("absolute-inverse", 9, 43, 3),
        ALPHA("ALPHA", 66, 324, 3),
        ATTRIBUTE_MEMBER("attribute-member", 34, 168, 3),
        ATTRIBUTE_SINGLETON("attribute-singleton", 37, 178, 4),
        CLOCK_SEQ_AND_RESERVED("clock-seq-and-reserved", 52, 252, 1),
        CLOCK_SEQ_LOW("clock-seq-low", 53, 253, 1),
        COLLECTION("collection", 31, 158, 3),
        COLLECTION_CONTEXT("collection-context", 23, 105, 12),
        COLLECTION_INVERSE("collection-inverse", 12, 54, 6),
        COLLECTION_RELATIVE("collection-relative", 8, 37, 6),
        CONTEXT("context", 19, 89, 4),
        CONTEXT_INVERSE("context-inverse", 11, 50, 4),
        CONTEXT_RELATIVE("context-relative", 7, 33, 4),
        CONTEXTUAL("contextual", 2, 13, 3),
        CR("CR", 70, 339, 1),
        CRLF("CRLF", 69, 336, 3),
        DATA_IRI("data-iri", 58, 272, 4),
        DATA_XREF("data-xref", 57, 268, 4),
        DIGIT("DIGIT", 67, 327, 1),
        DIRECT("direct", 3, 16, 5),
        DIRECT_RELATION("direct-relation", 14, 63, 6),
        ENTITY_MEMBER("entity-member", 33, 165, 3),
        ENTITY_SINGLETON("entity-singleton", 38, 182, 3),
        GENERIC("generic", 42, 199, 6),
        HEXDIG("HEXDIG", 68, 328, 8),
        HEXOCTET("hexoctet", 55, 256, 3),
        IMMUTABLE("immutable", 46, 224, 7),
        INNER_ROOT("inner-root", 27, 143, 4),
        INSTANCE("instance", 40, 188, 5),
        INVERSE("inverse", 4, 21, 5),
        INVERSE_RELATION("inverse-relation", 15, 69, 6),
        IPV6("ipv6", 56, 259, 9),
        IRI_CHAR("iri-char", 60, 285, 3),
        IRI_LITERAL("iri-literal", 28, 147, 5),
        IRI_SCHEME("iri-scheme", 59, 276, 9),
        IUNRESERVED("iunreserved", 63, 308, 8),
        LF("LF", 71, 340, 1),
        LITERAL("literal", 16, 75, 3),
        LITERAL_CONTEXT("literal-context", 25, 121, 13),
        LITERAL_REF("literal-ref", 18, 84, 5),
        LITERAL_VALUE("literal-value", 17, 78, 6),
        MEMBER("member", 32, 161, 4),
        MEMBER_CONTEXT("member-context", 24, 117, 4),
        MUTABLE("mutable", 45, 217, 7),
        NODE("node", 54, 254, 2),
        NONPAREN_DELIM("nonparen-delim", 61, 288, 17),
        ORDER_REF("order-ref", 35, 171, 4),
        ORGANIZATION("organization", 44, 211, 6),
        PCT_ENCODED("pct-encoded", 64, 316, 4),
        PEER("peer", 20, 93, 2),
        PEER_INVERSE("peer-inverse", 10, 46, 4),
        PEER_RELATIVE("peer-relative", 6, 29, 4),
        PEER_RELATIVE_CONTEXT("peer-relative-context", 21, 95, 3),
        PERSON("person", 43, 205, 6),
        RELATIONAL("relational", 13, 60, 3),
        RELATIVE_CONTEXT("relative-context", 22, 98, 7),
        SINGLETON("singleton", 36, 175, 3),
        SPECIFIC("specific", 41, 193, 6),
        SUBGRAPH("subgraph", 30, 154, 4),
        TIME_HIGH_AND_VERSION("time-high-and-version", 51, 250, 2),
        TIME_LOW("time-low", 49, 246, 2),
        TIME_MID("time-mid", 50, 248, 2),
        TYPE("type", 39, 185, 3),
        UCSCHAR("ucschar", 65, 320, 4),
        UUID("uuid", 48, 234, 12),
        XDI_CHAR("xdi-char", 62, 305, 3),
        XDI_GRAPH("xdi-graph", 0, 0, 9),
        XDI_LITERAL("xdi-literal", 29, 152, 2),
        XDI_SCHEME("xdi-scheme", 47, 231, 3),
        XDI_STATEMENT("xdi-statement", 1, 9, 4),
        XREF("xref", 26, 134, 9);
        private String name;
        private int id;
        private int offset;
        private int count;
        RuleNames(String string, int id, int offset, int count){
            this.name = string;
            this.id = id;
            this.offset = offset;
            this.count = count;
        }
        public  String ruleName(){return name;}
        public  int    ruleID(){return id;}
        private int    opcodeOffset(){return offset;}
        private int    opcodeCount(){return count;}
    }

    // UDT name enum
    public static int udtCount = 0;
    public enum UdtNames{
    }

    // private
    private static XDI3Grammar factoryInstance = null;
    private XDI3Grammar(Rule[] rules, Udt[] udts, Opcode[] opcodes){
        super(rules, udts, opcodes);
    }

    private static Rule[] getRules(){
    	Rule[] rules = new Rule[72];
        for(RuleNames r : RuleNames.values()){
            rules[r.ruleID()] = getRule(r.ruleID(), r.ruleName(), r.opcodeOffset(), r.opcodeCount());
        }
        return rules;
    }

    private static Udt[] getUdts(){
    	Udt[] udts = new Udt[0];
        return udts;
    }

        // opcodes
    private static Opcode[] getOpcodes(){
    	Opcode[] op = new Opcode[341];
        {int[] a = {1,2}; op[0] = getOpcodeCat(a);}
        op[1] = getOpcodeRnm(1, 9); // xdi-statement
        op[2] = getOpcodeRep((char)0, Character.MAX_VALUE, 3);
        {int[] a = {4,8}; op[3] = getOpcodeCat(a);}
        {int[] a = {5,6,7}; op[4] = getOpcodeAlt(a);}
        op[5] = getOpcodeRnm(70, 339); // CR
        op[6] = getOpcodeRnm(71, 340); // LF
        op[7] = getOpcodeRnm(69, 336); // CRLF
        op[8] = getOpcodeRnm(1, 9); // xdi-statement
        {int[] a = {10,11,12}; op[9] = getOpcodeAlt(a);}
        op[10] = getOpcodeRnm(2, 13); // contextual
        op[11] = getOpcodeRnm(13, 60); // relational
        op[12] = getOpcodeRnm(16, 75); // literal
        {int[] a = {14,15}; op[13] = getOpcodeAlt(a);}
        op[14] = getOpcodeRnm(3, 16); // direct
        op[15] = getOpcodeRnm(4, 21); // inverse
        {int[] a = {17,18,19,20}; op[16] = getOpcodeAlt(a);}
        op[17] = getOpcodeRnm(5, 26); // absolute
        op[18] = getOpcodeRnm(6, 29); // peer-relative
        op[19] = getOpcodeRnm(7, 33); // context-relative
        op[20] = getOpcodeRnm(8, 37); // collection-relative
        {int[] a = {22,23,24,25}; op[21] = getOpcodeAlt(a);}
        op[22] = getOpcodeRnm(9, 43); // absolute-inverse
        op[23] = getOpcodeRnm(10, 46); // peer-inverse
        op[24] = getOpcodeRnm(11, 50); // context-inverse
        op[25] = getOpcodeRnm(12, 54); // collection-inverse
        {int[] a = {27,28}; op[26] = getOpcodeCat(a);}
        {char[] a = {40,41,47,40,41,47}; op[27] = getOpcodeTls(a);}
        op[28] = getOpcodeRnm(19, 89); // context
        {int[] a = {30,31,32}; op[29] = getOpcodeCat(a);}
        op[30] = getOpcodeRnm(20, 93); // peer
        {char[] a = {47,40,41,47}; op[31] = getOpcodeTls(a);}
        op[32] = getOpcodeRnm(19, 89); // context
        {int[] a = {34,35,36}; op[33] = getOpcodeCat(a);}
        op[34] = getOpcodeRnm(19, 89); // context
        {char[] a = {47,40,41,47}; op[35] = getOpcodeTls(a);}
        op[36] = getOpcodeRnm(22, 98); // relative-context
        {int[] a = {38,40,41,42}; op[37] = getOpcodeCat(a);}
        op[38] = getOpcodeRep((char)0, (char)1, 39);
        op[39] = getOpcodeRnm(20, 93); // peer
        op[40] = getOpcodeRnm(23, 105); // collection-context
        {char[] a = {47,40,41,47}; op[41] = getOpcodeTls(a);}
        op[42] = getOpcodeRnm(24, 117); // member-context
        {int[] a = {44,45}; op[43] = getOpcodeCat(a);}
        op[44] = getOpcodeRnm(19, 89); // context
        {char[] a = {47,36,105,115,40,41,47,40,41}; op[45] = getOpcodeTls(a);}
        {int[] a = {47,48,49}; op[46] = getOpcodeCat(a);}
        op[47] = getOpcodeRnm(19, 89); // context
        {char[] a = {47,36,105,115,40,41,47}; op[48] = getOpcodeTls(a);}
        op[49] = getOpcodeRnm(20, 93); // peer
        {int[] a = {51,52,53}; op[50] = getOpcodeCat(a);}
        op[51] = getOpcodeRnm(22, 98); // relative-context
        {char[] a = {47,36,105,115,40,41,47}; op[52] = getOpcodeTls(a);}
        op[53] = getOpcodeRnm(19, 89); // context
        {int[] a = {55,56,57,59}; op[54] = getOpcodeCat(a);}
        op[55] = getOpcodeRnm(24, 117); // member-context
        {char[] a = {47,36,105,115,40,41,47}; op[56] = getOpcodeTls(a);}
        op[57] = getOpcodeRep((char)0, (char)1, 58);
        op[58] = getOpcodeRnm(20, 93); // peer
        op[59] = getOpcodeRnm(23, 105); // collection-context
        {int[] a = {61,62}; op[60] = getOpcodeAlt(a);}
        op[61] = getOpcodeRnm(14, 63); // direct-relation
        op[62] = getOpcodeRnm(15, 69); // inverse-relation
        {int[] a = {64,65,66,67,68}; op[63] = getOpcodeCat(a);}
        op[64] = getOpcodeRnm(19, 89); // context
        {char[] a = {47}; op[65] = getOpcodeTls(a);}
        op[66] = getOpcodeRnm(19, 89); // context
        {char[] a = {47}; op[67] = getOpcodeTls(a);}
        op[68] = getOpcodeRnm(19, 89); // context
        {int[] a = {70,71,72,73,74}; op[69] = getOpcodeCat(a);}
        op[70] = getOpcodeRnm(19, 89); // context
        {char[] a = {47,36,105,115}; op[71] = getOpcodeTls(a);}
        op[72] = getOpcodeRnm(19, 89); // context
        {char[] a = {47}; op[73] = getOpcodeTls(a);}
        op[74] = getOpcodeRnm(19, 89); // context
        {int[] a = {76,77}; op[75] = getOpcodeAlt(a);}
        op[76] = getOpcodeRnm(17, 78); // literal-value
        op[77] = getOpcodeRnm(18, 84); // literal-ref
        {int[] a = {79,81,82,83}; op[78] = getOpcodeCat(a);}
        op[79] = getOpcodeRep((char)0, (char)1, 80);
        op[80] = getOpcodeRnm(20, 93); // peer
        op[81] = getOpcodeRnm(25, 121); // literal-context
        {char[] a = {47,33,47}; op[82] = getOpcodeTls(a);}
        op[83] = getOpcodeRnm(57, 268); // data-xref
        {int[] a = {85,87,88}; op[84] = getOpcodeCat(a);}
        op[85] = getOpcodeRep((char)0, (char)1, 86);
        op[86] = getOpcodeRnm(20, 93); // peer
        op[87] = getOpcodeRnm(25, 121); // literal-context
        {char[] a = {47,33,47,40,41}; op[88] = getOpcodeTls(a);}
        {int[] a = {90,91,92}; op[89] = getOpcodeAlt(a);}
        op[90] = getOpcodeRnm(21, 95); // peer-relative-context
        op[91] = getOpcodeRnm(20, 93); // peer
        op[92] = getOpcodeRnm(22, 98); // relative-context
        op[93] = getOpcodeRep((char)1, Character.MAX_VALUE, 94);
        op[94] = getOpcodeRnm(26, 134); // xref
        {int[] a = {96,97}; op[95] = getOpcodeCat(a);}
        op[96] = getOpcodeRnm(20, 93); // peer
        op[97] = getOpcodeRnm(22, 98); // relative-context
        op[98] = getOpcodeRep((char)1, Character.MAX_VALUE, 99);
        {int[] a = {100,101}; op[99] = getOpcodeAlt(a);}
        op[100] = getOpcodeRnm(36, 175); // singleton
        {int[] a = {102,103}; op[101] = getOpcodeCat(a);}
        op[102] = getOpcodeRnm(31, 158); // collection
        op[103] = getOpcodeRep((char)0, (char)1, 104);
        op[104] = getOpcodeRnm(32, 161); // member
        {int[] a = {106,109,112,116}; op[105] = getOpcodeAlt(a);}
        {int[] a = {107,108}; op[106] = getOpcodeCat(a);}
        op[107] = getOpcodeRnm(36, 175); // singleton
        op[108] = getOpcodeRnm(23, 105); // collection-context
        {int[] a = {110,111}; op[109] = getOpcodeCat(a);}
        op[110] = getOpcodeRnm(31, 158); // collection
        op[111] = getOpcodeRnm(23, 105); // collection-context
        {int[] a = {113,114,115}; op[112] = getOpcodeCat(a);}
        op[113] = getOpcodeRnm(31, 158); // collection
        op[114] = getOpcodeRnm(32, 161); // member
        op[115] = getOpcodeRnm(23, 105); // collection-context
        op[116] = getOpcodeRnm(31, 158); // collection
        {int[] a = {118,119}; op[117] = getOpcodeCat(a);}
        op[118] = getOpcodeRnm(32, 161); // member
        op[119] = getOpcodeRep((char)0, (char)1, 120);
        op[120] = getOpcodeRnm(22, 98); // relative-context
        {int[] a = {122,125,128,132,133}; op[121] = getOpcodeAlt(a);}
        {int[] a = {123,124}; op[122] = getOpcodeCat(a);}
        op[123] = getOpcodeRnm(36, 175); // singleton
        op[124] = getOpcodeRnm(25, 121); // literal-context
        {int[] a = {126,127}; op[125] = getOpcodeCat(a);}
        op[126] = getOpcodeRnm(31, 158); // collection
        op[127] = getOpcodeRnm(25, 121); // literal-context
        {int[] a = {129,130,131}; op[128] = getOpcodeCat(a);}
        op[129] = getOpcodeRnm(31, 158); // collection
        op[130] = getOpcodeRnm(32, 161); // member
        op[131] = getOpcodeRnm(25, 121); // literal-context
        op[132] = getOpcodeRnm(34, 168); // attribute-member
        op[133] = getOpcodeRnm(37, 178); // attribute-singleton
        {int[] a = {135,136,142}; op[134] = getOpcodeCat(a);}
        {char[] a = {40}; op[135] = getOpcodeTls(a);}
        {int[] a = {137,138,139,140,141}; op[136] = getOpcodeAlt(a);}
        op[137] = getOpcodeRnm(19, 89); // context
        op[138] = getOpcodeRnm(27, 143); // inner-root
        op[139] = getOpcodeRnm(1, 9); // xdi-statement
        op[140] = getOpcodeRnm(28, 147); // iri-literal
        op[141] = getOpcodeRnm(29, 152); // xdi-literal
        {char[] a = {41}; op[142] = getOpcodeTls(a);}
        {int[] a = {144,145,146}; op[143] = getOpcodeCat(a);}
        op[144] = getOpcodeRnm(19, 89); // context
        {char[] a = {47}; op[145] = getOpcodeTls(a);}
        op[146] = getOpcodeRnm(19, 89); // context
        {int[] a = {148,149,150}; op[147] = getOpcodeCat(a);}
        op[148] = getOpcodeRnm(59, 276); // iri-scheme
        {char[] a = {58}; op[149] = getOpcodeTls(a);}
        op[150] = getOpcodeRep((char)0, Character.MAX_VALUE, 151);
        op[151] = getOpcodeRnm(60, 285); // iri-char
        op[152] = getOpcodeRep((char)1, Character.MAX_VALUE, 153);
        op[153] = getOpcodeRnm(62, 305); // xdi-char
        {int[] a = {155,156,157}; op[154] = getOpcodeAlt(a);}
        op[155] = getOpcodeRnm(31, 158); // collection
        op[156] = getOpcodeRnm(32, 161); // member
        op[157] = getOpcodeRnm(36, 175); // singleton
        {int[] a = {159,160}; op[158] = getOpcodeCat(a);}
        {char[] a = {38}; op[159] = getOpcodeTls(a);}
        op[160] = getOpcodeRnm(26, 134); // xref
        {int[] a = {162,163,164}; op[161] = getOpcodeAlt(a);}
        op[162] = getOpcodeRnm(33, 165); // entity-member
        op[163] = getOpcodeRnm(34, 168); // attribute-member
        op[164] = getOpcodeRnm(35, 171); // order-ref
        {int[] a = {166,167}; op[165] = getOpcodeCat(a);}
        {char[] a = {58}; op[166] = getOpcodeTls(a);}
        op[167] = getOpcodeRnm(26, 134); // xref
        {int[] a = {169,170}; op[168] = getOpcodeCat(a);}
        {char[] a = {59}; op[169] = getOpcodeTls(a);}
        op[170] = getOpcodeRnm(26, 134); // xref
        {int[] a = {172,173}; op[171] = getOpcodeCat(a);}
        {char[] a = {36}; op[172] = getOpcodeTls(a);}
        op[173] = getOpcodeRep((char)1, Character.MAX_VALUE, 174);
        op[174] = getOpcodeRnm(67, 327); // DIGIT
        {int[] a = {176,177}; op[175] = getOpcodeAlt(a);}
        op[176] = getOpcodeRnm(37, 178); // attribute-singleton
        op[177] = getOpcodeRnm(38, 182); // entity-singleton
        {int[] a = {179,180,181}; op[178] = getOpcodeCat(a);}
        {char[] a = {44,40}; op[179] = getOpcodeTls(a);}
        op[180] = getOpcodeRnm(39, 185); // type
        {char[] a = {41}; op[181] = getOpcodeTls(a);}
        {int[] a = {183,184}; op[182] = getOpcodeAlt(a);}
        op[183] = getOpcodeRnm(39, 185); // type
        op[184] = getOpcodeRnm(40, 188); // instance
        {int[] a = {186,187}; op[185] = getOpcodeAlt(a);}
        op[186] = getOpcodeRnm(41, 193); // specific
        op[187] = getOpcodeRnm(42, 199); // generic
        {int[] a = {189,190,191,192}; op[188] = getOpcodeAlt(a);}
        op[189] = getOpcodeRnm(43, 205); // person
        op[190] = getOpcodeRnm(44, 211); // organization
        op[191] = getOpcodeRnm(45, 217); // mutable
        op[192] = getOpcodeRnm(46, 224); // immutable
        {int[] a = {194,195}; op[193] = getOpcodeCat(a);}
        {char[] a = {36}; op[194] = getOpcodeTls(a);}
        op[195] = getOpcodeRep((char)0, (char)1, 196);
        {int[] a = {197,198}; op[196] = getOpcodeAlt(a);}
        op[197] = getOpcodeRnm(26, 134); // xref
        op[198] = getOpcodeRnm(29, 152); // xdi-literal
        {int[] a = {200,201}; op[199] = getOpcodeCat(a);}
        {char[] a = {43}; op[200] = getOpcodeTls(a);}
        op[201] = getOpcodeRep((char)0, (char)1, 202);
        {int[] a = {203,204}; op[202] = getOpcodeAlt(a);}
        op[203] = getOpcodeRnm(26, 134); // xref
        op[204] = getOpcodeRnm(29, 152); // xdi-literal
        {int[] a = {206,207}; op[205] = getOpcodeCat(a);}
        {char[] a = {61}; op[206] = getOpcodeTls(a);}
        op[207] = getOpcodeRep((char)0, (char)1, 208);
        {int[] a = {209,210}; op[208] = getOpcodeAlt(a);}
        op[209] = getOpcodeRnm(26, 134); // xref
        op[210] = getOpcodeRnm(29, 152); // xdi-literal
        {int[] a = {212,213}; op[211] = getOpcodeCat(a);}
        {char[] a = {64}; op[212] = getOpcodeTls(a);}
        op[213] = getOpcodeRep((char)0, (char)1, 214);
        {int[] a = {215,216}; op[214] = getOpcodeAlt(a);}
        op[215] = getOpcodeRnm(26, 134); // xref
        op[216] = getOpcodeRnm(29, 152); // xdi-literal
        {int[] a = {218,219}; op[217] = getOpcodeCat(a);}
        {char[] a = {42}; op[218] = getOpcodeTls(a);}
        op[219] = getOpcodeRep((char)0, (char)1, 220);
        {int[] a = {221,222,223}; op[220] = getOpcodeAlt(a);}
        op[221] = getOpcodeRnm(26, 134); // xref
        op[222] = getOpcodeRnm(47, 231); // xdi-scheme
        op[223] = getOpcodeRnm(29, 152); // xdi-literal
        {int[] a = {225,226}; op[224] = getOpcodeCat(a);}
        {char[] a = {33}; op[225] = getOpcodeTls(a);}
        op[226] = getOpcodeRep((char)0, (char)1, 227);
        {int[] a = {228,229,230}; op[227] = getOpcodeAlt(a);}
        op[228] = getOpcodeRnm(26, 134); // xref
        op[229] = getOpcodeRnm(47, 231); // xdi-scheme
        op[230] = getOpcodeRnm(29, 152); // xdi-literal
        {int[] a = {232,233}; op[231] = getOpcodeAlt(a);}
        op[232] = getOpcodeRnm(48, 234); // uuid
        op[233] = getOpcodeRnm(56, 259); // ipv6
        {int[] a = {235,236,237,238,239,240,241,242,243,244,245}; op[234] = getOpcodeCat(a);}
        {char[] a = {58,117,117,105,100,58}; op[235] = getOpcodeTls(a);}
        op[236] = getOpcodeRnm(49, 246); // time-low
        {char[] a = {45}; op[237] = getOpcodeTls(a);}
        op[238] = getOpcodeRnm(50, 248); // time-mid
        {char[] a = {45}; op[239] = getOpcodeTls(a);}
        op[240] = getOpcodeRnm(51, 250); // time-high-and-version
        {char[] a = {45}; op[241] = getOpcodeTls(a);}
        op[242] = getOpcodeRnm(52, 252); // clock-seq-and-reserved
        op[243] = getOpcodeRnm(53, 253); // clock-seq-low
        {char[] a = {45}; op[244] = getOpcodeTls(a);}
        op[245] = getOpcodeRnm(54, 254); // node
        op[246] = getOpcodeRep((char)4, Character.MAX_VALUE, 247);
        op[247] = getOpcodeRnm(55, 256); // hexoctet
        op[248] = getOpcodeRep((char)2, Character.MAX_VALUE, 249);
        op[249] = getOpcodeRnm(55, 256); // hexoctet
        op[250] = getOpcodeRep((char)2, Character.MAX_VALUE, 251);
        op[251] = getOpcodeRnm(55, 256); // hexoctet
        op[252] = getOpcodeRnm(55, 256); // hexoctet
        op[253] = getOpcodeRnm(55, 256); // hexoctet
        op[254] = getOpcodeRep((char)6, Character.MAX_VALUE, 255);
        op[255] = getOpcodeRnm(55, 256); // hexoctet
        {int[] a = {257,258}; op[256] = getOpcodeCat(a);}
        op[257] = getOpcodeRnm(68, 328); // HEXDIG
        op[258] = getOpcodeRnm(68, 328); // HEXDIG
        {int[] a = {260,261,263}; op[259] = getOpcodeCat(a);}
        {char[] a = {58,105,112,118,54,58}; op[260] = getOpcodeTls(a);}
        op[261] = getOpcodeRep((char)4, (char)4, 262);
        op[262] = getOpcodeRnm(68, 328); // HEXDIG
        op[263] = getOpcodeRep((char)7, Character.MAX_VALUE, 264);
        {int[] a = {265,266}; op[264] = getOpcodeCat(a);}
        {char[] a = {58}; op[265] = getOpcodeTls(a);}
        op[266] = getOpcodeRep((char)4, (char)4, 267);
        op[267] = getOpcodeRnm(68, 328); // HEXDIG
        {int[] a = {269,270,271}; op[268] = getOpcodeCat(a);}
        {char[] a = {40}; op[269] = getOpcodeTls(a);}
        op[270] = getOpcodeRnm(58, 272); // data-iri
        {char[] a = {41}; op[271] = getOpcodeTls(a);}
        {int[] a = {273,274}; op[272] = getOpcodeCat(a);}
        {char[] a = {100,97,116,97,58,44}; op[273] = getOpcodeTls(a);}
        op[274] = getOpcodeRep((char)1, Character.MAX_VALUE, 275);
        op[275] = getOpcodeRnm(60, 285); // iri-char
        {int[] a = {277,278}; op[276] = getOpcodeCat(a);}
        op[277] = getOpcodeRnm(66, 324); // ALPHA
        op[278] = getOpcodeRep((char)0, Character.MAX_VALUE, 279);
        {int[] a = {280,281,282,283,284}; op[279] = getOpcodeAlt(a);}
        op[280] = getOpcodeRnm(66, 324); // ALPHA
        op[281] = getOpcodeRnm(67, 327); // DIGIT
        {char[] a = {43}; op[282] = getOpcodeTls(a);}
        {char[] a = {45}; op[283] = getOpcodeTls(a);}
        {char[] a = {46}; op[284] = getOpcodeTls(a);}
        {int[] a = {286,287}; op[285] = getOpcodeAlt(a);}
        op[286] = getOpcodeRnm(62, 305); // xdi-char
        op[287] = getOpcodeRnm(61, 288); // nonparen-delim
        {int[] a = {289,290,291,292,293,294,295,296,297,298,299,300,301,302,303,304}; op[288] = getOpcodeAlt(a);}
        {char[] a = {58}; op[289] = getOpcodeTls(a);}
        {char[] a = {47}; op[290] = getOpcodeTls(a);}
        {char[] a = {63}; op[291] = getOpcodeTls(a);}
        {char[] a = {35}; op[292] = getOpcodeTls(a);}
        {char[] a = {91}; op[293] = getOpcodeTls(a);}
        {char[] a = {93}; op[294] = getOpcodeTls(a);}
        {char[] a = {64}; op[295] = getOpcodeTls(a);}
        {char[] a = {33}; op[296] = getOpcodeTls(a);}
        {char[] a = {36}; op[297] = getOpcodeTls(a);}
        {char[] a = {38}; op[298] = getOpcodeTls(a);}
        {char[] a = {39}; op[299] = getOpcodeTls(a);}
        {char[] a = {42}; op[300] = getOpcodeTls(a);}
        {char[] a = {43}; op[301] = getOpcodeTls(a);}
        {char[] a = {44}; op[302] = getOpcodeTls(a);}
        {char[] a = {59}; op[303] = getOpcodeTls(a);}
        {char[] a = {61}; op[304] = getOpcodeTls(a);}
        {int[] a = {306,307}; op[305] = getOpcodeAlt(a);}
        op[306] = getOpcodeRnm(63, 308); // iunreserved
        op[307] = getOpcodeRnm(64, 316); // pct-encoded
        {int[] a = {309,310,311,312,313,314,315}; op[308] = getOpcodeAlt(a);}
        op[309] = getOpcodeRnm(66, 324); // ALPHA
        op[310] = getOpcodeRnm(67, 327); // DIGIT
        {char[] a = {45}; op[311] = getOpcodeTls(a);}
        {char[] a = {46}; op[312] = getOpcodeTls(a);}
        {char[] a = {95}; op[313] = getOpcodeTls(a);}
        {char[] a = {126}; op[314] = getOpcodeTls(a);}
        op[315] = getOpcodeRnm(65, 320); // ucschar
        {int[] a = {317,318,319}; op[316] = getOpcodeCat(a);}
        {char[] a = {37}; op[317] = getOpcodeTls(a);}
        op[318] = getOpcodeRnm(68, 328); // HEXDIG
        op[319] = getOpcodeRnm(68, 328); // HEXDIG
        {int[] a = {321,322,323}; op[320] = getOpcodeAlt(a);}
        op[321] = getOpcodeTrg((char)160, (char)55295);
        op[322] = getOpcodeTrg((char)63744, (char)64975);
        op[323] = getOpcodeTrg((char)65008, (char)65519);
        {int[] a = {325,326}; op[324] = getOpcodeAlt(a);}
        op[325] = getOpcodeTrg((char)65, (char)90);
        op[326] = getOpcodeTrg((char)97, (char)122);
        op[327] = getOpcodeTrg((char)48, (char)57);
        {int[] a = {329,330,331,332,333,334,335}; op[328] = getOpcodeAlt(a);}
        op[329] = getOpcodeRnm(67, 327); // DIGIT
        {char[] a = {65}; op[330] = getOpcodeTls(a);}
        {char[] a = {66}; op[331] = getOpcodeTls(a);}
        {char[] a = {67}; op[332] = getOpcodeTls(a);}
        {char[] a = {68}; op[333] = getOpcodeTls(a);}
        {char[] a = {69}; op[334] = getOpcodeTls(a);}
        {char[] a = {70}; op[335] = getOpcodeTls(a);}
        {int[] a = {337,338}; op[336] = getOpcodeCat(a);}
        op[337] = getOpcodeRnm(70, 339); // CR
        op[338] = getOpcodeRnm(71, 340); // LF
        {char[] a = {13}; op[339] = getOpcodeTbs(a);}
        {char[] a = {10}; op[340] = getOpcodeTbs(a);}
        return op;
    }

    public static void display(PrintStream out){
        out.println(";");
        out.println("; xdi2.core.xri3.parser.full.apg.XDI3Grammar");
        out.println(";");
        out.println("xdi-graph               = xdi-statement *( ( CR / LF / CRLF ) xdi-statement )");
        out.println("xdi-statement           = contextual / relational / literal");
        out.println("");
        out.println("contextual              = direct / inverse");
        out.println("direct                  = absolute / peer-relative / context-relative / collection-relative");
        out.println("inverse                 = absolute-inverse / peer-inverse / context-inverse / collection-inverse");
        out.println("absolute                = \"()/()/\" context");
        out.println("peer-relative           = peer \"/()/\" context");
        out.println("context-relative        = context \"/()/\" relative-context");
        out.println("collection-relative     = [ peer ] collection-context \"/()/\" member-context");
        out.println("absolute-inverse        = context \"/$is()/()\"");
        out.println("peer-inverse            = context \"/$is()/\" peer");
        out.println("context-inverse         = relative-context \"/$is()/\" context");
        out.println("collection-inverse      = member-context \"/$is()/\" [ peer ] collection-context");
        out.println("");
        out.println("relational              = direct-relation / inverse-relation");
        out.println("direct-relation         = context \"/\" context \"/\" context");
        out.println("inverse-relation        = context \"/$is\" context \"/\" context");
        out.println("");
        out.println("literal                 = literal-value / literal-ref");
        out.println("literal-value           = [ peer ] literal-context \"/!/\" data-xref");
        out.println("literal-ref             = [ peer ] literal-context \"/!/()\"");
        out.println("");
        out.println("context                 = peer-relative-context / peer / relative-context");
        out.println("peer                    = 1*xref");
        out.println("peer-relative-context   = peer relative-context");
        out.println("relative-context        = 1*( singleton / ( collection [ member ] ) )");
        out.println("");
        out.println("collection-context      = ( singleton collection-context ) / ( collection collection-context ) / ( collection member collection-context ) / collection");
        out.println("");
        out.println("member-context          = member [ relative-context ]");
        out.println("");
        out.println("literal-context         = ( singleton literal-context ) / ( collection literal-context ) / ( collection member literal-context ) / attribute-member / attribute-singleton");
        out.println("");
        out.println("xref                    = \"(\" ( context / inner-root / xdi-statement / iri-literal / xdi-literal ) \")\"");
        out.println("inner-root              = context \"/\" context");
        out.println("iri-literal             = iri-scheme \":\" *iri-char");
        out.println("xdi-literal             = 1*xdi-char");
        out.println("");
        out.println("subgraph                = collection / member / singleton");
        out.println("");
        out.println("collection              = \"&\" xref");
        out.println("");
        out.println("member                  = entity-member / attribute-member / order-ref");
        out.println("entity-member           = \":\" xref");
        out.println("attribute-member        = \";\" xref");
        out.println("order-ref               = \"$\" 1*DIGIT");
        out.println("");
        out.println("singleton               = attribute-singleton / entity-singleton");
        out.println("");
        out.println("attribute-singleton     = \",(\" type \")\"");
        out.println("");
        out.println("entity-singleton        = type / instance");
        out.println("type                    = specific / generic");
        out.println("instance                = person / organization / mutable / immutable");
        out.println("specific                = \"$\" [ xref / xdi-literal ]");
        out.println("generic                 = \"+\" [ xref / xdi-literal ]");
        out.println("person                  = \"=\" [ xref / xdi-literal ]");
        out.println("organization            = \"@\" [ xref / xdi-literal ]");
        out.println("mutable                 = \"*\" [ xref / xdi-scheme / xdi-literal ]");
        out.println("immutable               = \"!\" [ xref / xdi-scheme / xdi-literal ]");
        out.println("xdi-scheme              = uuid / ipv6");
        out.println("uuid                    = \":uuid:\" time-low \"-\" time-mid \"-\" time-high-and-version \"-\" clock-seq-and-reserved clock-seq-low \"-\" node");
        out.println("time-low                = 4*hexoctet");
        out.println("time-mid                = 2*hexoctet");
        out.println("time-high-and-version   = 2*hexoctet");
        out.println("clock-seq-and-reserved  = hexoctet");
        out.println("clock-seq-low           = hexoctet");
        out.println("node                    = 6*hexoctet");
        out.println("hexoctet                = HEXDIG HEXDIG");
        out.println("ipv6                    = \":ipv6:\" 4HEXDIG 7*( \":\" 4HEXDIG )");
        out.println("");
        out.println("data-xref               = \"(\" data-iri \")\"");
        out.println("data-iri                = \"data:,\" 1*iri-char");
        out.println("");
        out.println("iri-scheme              = ALPHA *( ALPHA / DIGIT / \"+\" / \"-\" / \".\" )  ");
        out.println("iri-char                = xdi-char / nonparen-delim                                          ; \"(\" and \")\" are excluded");
        out.println("nonparen-delim          = \":\" / \"/\" / \"?\" / \"#\" / \"[\" / \"]\" / \"@\" / \"!\" / \"$\" / \"&\" / \"'\" / \"*\" / \"+\" / \",\" / \";\" / \"=\"");
        out.println("");
        out.println("xdi-char                = iunreserved / pct-encoded");
        out.println("iunreserved             = ALPHA / DIGIT / \"-\" / \".\" / \"_\" / \"~\" / ucschar");
        out.println("pct-encoded             = \"%\" HEXDIG HEXDIG");
        out.println("ucschar                 = %xA0-D7FF / %xF900-FDCF / %xFDF0-FFEF");
        out.println("ALPHA                   =  %x41-5A / %x61-7A   ; A-Z / a-z");
        out.println("DIGIT                   =  %x30-39     ; 0-9");
        out.println("HEXDIG                  =  DIGIT / \"A\" / \"B\" / \"C\" / \"D\" / \"E\" / \"F\"");
        out.println("CRLF                    = CR LF");
        out.println("CR                      = %x0D");
        out.println("LF                      = %x0A");
    }
}
