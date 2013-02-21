package xdi2.core.xri3.parser.apg;

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
    public static int ruleCount = 68;
    public enum RuleNames{
        ALPHA("ALPHA", 52, 354, 3),
        BIT("BIT", 53, 357, 3),
        CHAR("CHAR", 54, 360, 1),
        CR("CR", 55, 361, 1),
        CRLF("CRLF", 56, 362, 3),
        CTL("CTL", 57, 365, 3),
        DEC_OCTET("dec-octet", 32, 245, 16),
        DIGIT("DIGIT", 58, 368, 1),
        DQUOTE("DQUOTE", 59, 369, 1),
        GCS_CHAR("gcs-char", 9, 29, 5),
        GEN_DELIMS("gen-delims", 49, 329, 8),
        GLOBAL_SUBSEG("global-subseg", 7, 16, 7),
        H16("h16", 30, 235, 2),
        HEXDIG("HEXDIG", 60, 370, 8),
        HTAB("HTAB", 61, 378, 1),
        IAUTHORITY("iauthority", 23, 101, 10),
        IFRAGMENT("ifragment", 43, 299, 5),
        IHIER_PART("ihier-part", 22, 93, 8),
        IHOST("ihost", 25, 117, 4),
        IP_LITERAL("IP-literal", 26, 121, 6),
        IPATH_ABEMPTY("ipath-abempty", 35, 268, 4),
        IPATH_ABS("ipath-abs", 36, 272, 9),
        IPATH_EMPTY("ipath-empty", 38, 287, 1),
        IPATH_ROOTLESS("ipath-rootless", 37, 281, 6),
        IPCHAR("ipchar", 44, 304, 6),
        IPRIVATE("iprivate", 42, 298, 1),
        IPV4ADDRESS("IPv4address", 31, 237, 8),
        IPV6ADDRESS("IPv6address", 28, 137, 92),
        IPVFUTURE("IPvFuture", 27, 127, 10),
        IQUERY("iquery", 41, 292, 6),
        IREG_NAME("ireg-name", 33, 261, 5),
        IRI("IRI", 20, 72, 12),
        ISEGMENT("isegment", 39, 288, 2),
        ISEGMENT_NZ("isegment-nz", 40, 290, 2),
        IUNRESERVED("iunreserved", 45, 310, 8),
        IUSERINFO("iuserinfo", 24, 111, 6),
        LCS_CHAR("lcs-char", 10, 34, 3),
        LF("LF", 62, 379, 1),
        LITERAL("literal", 18, 67, 2),
        LOCAL_SUBSEG("local-subseg", 8, 23, 6),
        LS32("ls32", 29, 229, 6),
        LWSP("LWSP", 63, 380, 6),
        OCTET("OCTET", 64, 386, 1),
        PCT_ENCODED("pct-encoded", 46, 318, 4),
        PORT("port", 34, 266, 2),
        RESERVED("reserved", 48, 326, 3),
        SCHEME("scheme", 21, 84, 9),
        SP("SP", 65, 387, 1),
        SUB_DELIMS("sub-delims", 50, 337, 10),
        SUBSEG("subseg", 6, 12, 4),
        UCSCHAR("ucschar", 47, 322, 4),
        UNRESERVED("unreserved", 51, 347, 7),
        VCHAR("VCHAR", 66, 388, 1),
        WSP("WSP", 67, 389, 3),
        XDI_CONTEXT("xdi-context", 0, 0, 1),
        XDI_OBJECT("xdi-object", 4, 9, 1),
        XDI_PCHAR("xdi-pchar", 19, 69, 3),
        XDI_PREDICATE("xdi-predicate", 3, 8, 1),
        XDI_SEGMENT("xdi-segment", 5, 10, 2),
        XDI_STATEMENT("xdi-statement", 1, 1, 6),
        XDI_SUBJECT("xdi-subject", 2, 7, 1),
        XREF("xref", 11, 37, 7),
        XREF_EMPTY("xref-empty", 12, 44, 1),
        XREF_IRI("xref-IRI", 13, 45, 4),
        XREF_LITERAL("xref-literal", 17, 63, 4),
        XREF_SEGMENT("xref-segment", 14, 49, 4),
        XREF_STATEMENT("xref-statement", 16, 59, 4),
        XREF_SUBJECT_PREDICATE("xref-subject-predicate", 15, 53, 6);
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
    	Rule[] rules = new Rule[68];
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
    	Opcode[] op = new Opcode[392];
        op[0] = getOpcodeRnm(5, 10); // xdi-segment
        {int[] a = {2,3,4,5,6}; op[1] = getOpcodeCat(a);}
        op[2] = getOpcodeRnm(2, 7); // xdi-subject
        {char[] a = {47}; op[3] = getOpcodeTls(a);}
        op[4] = getOpcodeRnm(3, 8); // xdi-predicate
        {char[] a = {47}; op[5] = getOpcodeTls(a);}
        op[6] = getOpcodeRnm(4, 9); // xdi-object
        op[7] = getOpcodeRnm(5, 10); // xdi-segment
        op[8] = getOpcodeRnm(5, 10); // xdi-segment
        op[9] = getOpcodeRnm(5, 10); // xdi-segment
        op[10] = getOpcodeRep((char)1, Character.MAX_VALUE, 11);
        op[11] = getOpcodeRnm(6, 12); // subseg
        {int[] a = {13,14,15}; op[12] = getOpcodeAlt(a);}
        op[13] = getOpcodeRnm(7, 16); // global-subseg
        op[14] = getOpcodeRnm(8, 23); // local-subseg
        op[15] = getOpcodeRnm(11, 37); // xref
        {int[] a = {17,18}; op[16] = getOpcodeCat(a);}
        op[17] = getOpcodeRnm(9, 29); // gcs-char
        op[18] = getOpcodeRep((char)0, (char)1, 19);
        {int[] a = {20,21,22}; op[19] = getOpcodeAlt(a);}
        op[20] = getOpcodeRnm(8, 23); // local-subseg
        op[21] = getOpcodeRnm(11, 37); // xref
        op[22] = getOpcodeRnm(18, 67); // literal
        {int[] a = {24,25}; op[23] = getOpcodeCat(a);}
        op[24] = getOpcodeRnm(10, 34); // lcs-char
        op[25] = getOpcodeRep((char)0, (char)1, 26);
        {int[] a = {27,28}; op[26] = getOpcodeAlt(a);}
        op[27] = getOpcodeRnm(11, 37); // xref
        op[28] = getOpcodeRnm(18, 67); // literal
        {int[] a = {30,31,32,33}; op[29] = getOpcodeAlt(a);}
        {char[] a = {61}; op[30] = getOpcodeTls(a);}
        {char[] a = {64}; op[31] = getOpcodeTls(a);}
        {char[] a = {43}; op[32] = getOpcodeTls(a);}
        {char[] a = {36}; op[33] = getOpcodeTls(a);}
        {int[] a = {35,36}; op[34] = getOpcodeAlt(a);}
        {char[] a = {42}; op[35] = getOpcodeTls(a);}
        {char[] a = {33}; op[36] = getOpcodeTls(a);}
        {int[] a = {38,39,40,41,42,43}; op[37] = getOpcodeAlt(a);}
        op[38] = getOpcodeRnm(12, 44); // xref-empty
        op[39] = getOpcodeRnm(13, 45); // xref-IRI
        op[40] = getOpcodeRnm(14, 49); // xref-segment
        op[41] = getOpcodeRnm(15, 53); // xref-subject-predicate
        op[42] = getOpcodeRnm(16, 59); // xref-statement
        op[43] = getOpcodeRnm(17, 63); // xref-literal
        {char[] a = {40,41}; op[44] = getOpcodeTls(a);}
        {int[] a = {46,47,48}; op[45] = getOpcodeCat(a);}
        {char[] a = {40}; op[46] = getOpcodeTls(a);}
        op[47] = getOpcodeRnm(20, 72); // IRI
        {char[] a = {41}; op[48] = getOpcodeTls(a);}
        {int[] a = {50,51,52}; op[49] = getOpcodeCat(a);}
        {char[] a = {40}; op[50] = getOpcodeTls(a);}
        op[51] = getOpcodeRnm(5, 10); // xdi-segment
        {char[] a = {41}; op[52] = getOpcodeTls(a);}
        {int[] a = {54,55,56,57,58}; op[53] = getOpcodeCat(a);}
        {char[] a = {40}; op[54] = getOpcodeTls(a);}
        op[55] = getOpcodeRnm(2, 7); // xdi-subject
        {char[] a = {47}; op[56] = getOpcodeTls(a);}
        op[57] = getOpcodeRnm(3, 8); // xdi-predicate
        {char[] a = {41}; op[58] = getOpcodeTls(a);}
        {int[] a = {60,61,62}; op[59] = getOpcodeCat(a);}
        {char[] a = {40}; op[60] = getOpcodeTls(a);}
        op[61] = getOpcodeRnm(1, 1); // xdi-statement
        {char[] a = {41}; op[62] = getOpcodeTls(a);}
        {int[] a = {64,65,66}; op[63] = getOpcodeCat(a);}
        {char[] a = {40}; op[64] = getOpcodeTls(a);}
        op[65] = getOpcodeRnm(18, 67); // literal
        {char[] a = {41}; op[66] = getOpcodeTls(a);}
        op[67] = getOpcodeRep((char)1, Character.MAX_VALUE, 68);
        op[68] = getOpcodeRnm(19, 69); // xdi-pchar
        {int[] a = {70,71}; op[69] = getOpcodeAlt(a);}
        op[70] = getOpcodeRnm(45, 310); // iunreserved
        op[71] = getOpcodeRnm(46, 318); // pct-encoded
        {int[] a = {73,74,75,76,80}; op[72] = getOpcodeCat(a);}
        op[73] = getOpcodeRnm(21, 84); // scheme
        {char[] a = {58}; op[74] = getOpcodeTls(a);}
        op[75] = getOpcodeRnm(22, 93); // ihier-part
        op[76] = getOpcodeRep((char)0, (char)1, 77);
        {int[] a = {78,79}; op[77] = getOpcodeCat(a);}
        {char[] a = {63}; op[78] = getOpcodeTls(a);}
        op[79] = getOpcodeRnm(41, 292); // iquery
        op[80] = getOpcodeRep((char)0, (char)1, 81);
        {int[] a = {82,83}; op[81] = getOpcodeCat(a);}
        {char[] a = {35}; op[82] = getOpcodeTls(a);}
        op[83] = getOpcodeRnm(43, 299); // ifragment
        {int[] a = {85,86}; op[84] = getOpcodeCat(a);}
        op[85] = getOpcodeRnm(52, 354); // ALPHA
        op[86] = getOpcodeRep((char)0, Character.MAX_VALUE, 87);
        {int[] a = {88,89,90,91,92}; op[87] = getOpcodeAlt(a);}
        op[88] = getOpcodeRnm(52, 354); // ALPHA
        op[89] = getOpcodeRnm(58, 368); // DIGIT
        {char[] a = {43}; op[90] = getOpcodeTls(a);}
        {char[] a = {45}; op[91] = getOpcodeTls(a);}
        {char[] a = {46}; op[92] = getOpcodeTls(a);}
        {int[] a = {94,98,99,100}; op[93] = getOpcodeAlt(a);}
        {int[] a = {95,96,97}; op[94] = getOpcodeCat(a);}
        {char[] a = {47,47}; op[95] = getOpcodeTls(a);}
        op[96] = getOpcodeRnm(23, 101); // iauthority
        op[97] = getOpcodeRnm(35, 268); // ipath-abempty
        op[98] = getOpcodeRnm(36, 272); // ipath-abs
        op[99] = getOpcodeRnm(37, 281); // ipath-rootless
        op[100] = getOpcodeRnm(38, 287); // ipath-empty
        {int[] a = {102,106,107}; op[101] = getOpcodeCat(a);}
        op[102] = getOpcodeRep((char)0, (char)1, 103);
        {int[] a = {104,105}; op[103] = getOpcodeCat(a);}
        op[104] = getOpcodeRnm(24, 111); // iuserinfo
        {char[] a = {64}; op[105] = getOpcodeTls(a);}
        op[106] = getOpcodeRnm(25, 117); // ihost
        op[107] = getOpcodeRep((char)0, (char)1, 108);
        {int[] a = {109,110}; op[108] = getOpcodeCat(a);}
        {char[] a = {58}; op[109] = getOpcodeTls(a);}
        op[110] = getOpcodeRnm(34, 266); // port
        op[111] = getOpcodeRep((char)0, Character.MAX_VALUE, 112);
        {int[] a = {113,114,115,116}; op[112] = getOpcodeAlt(a);}
        op[113] = getOpcodeRnm(45, 310); // iunreserved
        op[114] = getOpcodeRnm(46, 318); // pct-encoded
        op[115] = getOpcodeRnm(50, 337); // sub-delims
        {char[] a = {58}; op[116] = getOpcodeTls(a);}
        {int[] a = {118,119,120}; op[117] = getOpcodeAlt(a);}
        op[118] = getOpcodeRnm(26, 121); // IP-literal
        op[119] = getOpcodeRnm(31, 237); // IPv4address
        op[120] = getOpcodeRnm(33, 261); // ireg-name
        {int[] a = {122,123,126}; op[121] = getOpcodeCat(a);}
        {char[] a = {91}; op[122] = getOpcodeTls(a);}
        {int[] a = {124,125}; op[123] = getOpcodeAlt(a);}
        op[124] = getOpcodeRnm(28, 137); // IPv6address
        op[125] = getOpcodeRnm(27, 127); // IPvFuture
        {char[] a = {93}; op[126] = getOpcodeTls(a);}
        {int[] a = {128,129,131,132}; op[127] = getOpcodeCat(a);}
        {char[] a = {118}; op[128] = getOpcodeTls(a);}
        op[129] = getOpcodeRep((char)1, Character.MAX_VALUE, 130);
        op[130] = getOpcodeRnm(60, 370); // HEXDIG
        {char[] a = {46}; op[131] = getOpcodeTls(a);}
        op[132] = getOpcodeRep((char)1, Character.MAX_VALUE, 133);
        {int[] a = {134,135,136}; op[133] = getOpcodeAlt(a);}
        op[134] = getOpcodeRnm(51, 347); // unreserved
        op[135] = getOpcodeRnm(50, 337); // sub-delims
        {char[] a = {58}; op[136] = getOpcodeTls(a);}
        {int[] a = {138,144,151,160,174,188,200,210,220}; op[137] = getOpcodeAlt(a);}
        {int[] a = {139,143}; op[138] = getOpcodeCat(a);}
        op[139] = getOpcodeRep((char)6, (char)6, 140);
        {int[] a = {141,142}; op[140] = getOpcodeCat(a);}
        op[141] = getOpcodeRnm(30, 235); // h16
        {char[] a = {58}; op[142] = getOpcodeTls(a);}
        op[143] = getOpcodeRnm(29, 229); // ls32
        {int[] a = {145,146,150}; op[144] = getOpcodeCat(a);}
        {char[] a = {58,58}; op[145] = getOpcodeTls(a);}
        op[146] = getOpcodeRep((char)5, (char)5, 147);
        {int[] a = {148,149}; op[147] = getOpcodeCat(a);}
        op[148] = getOpcodeRnm(30, 235); // h16
        {char[] a = {58}; op[149] = getOpcodeTls(a);}
        op[150] = getOpcodeRnm(29, 229); // ls32
        {int[] a = {152,154,155,159}; op[151] = getOpcodeCat(a);}
        op[152] = getOpcodeRep((char)0, (char)1, 153);
        op[153] = getOpcodeRnm(30, 235); // h16
        {char[] a = {58,58}; op[154] = getOpcodeTls(a);}
        op[155] = getOpcodeRep((char)4, (char)4, 156);
        {int[] a = {157,158}; op[156] = getOpcodeCat(a);}
        op[157] = getOpcodeRnm(30, 235); // h16
        {char[] a = {58}; op[158] = getOpcodeTls(a);}
        op[159] = getOpcodeRnm(29, 229); // ls32
        {int[] a = {161,168,169,173}; op[160] = getOpcodeCat(a);}
        op[161] = getOpcodeRep((char)0, (char)1, 162);
        {int[] a = {163,167}; op[162] = getOpcodeCat(a);}
        op[163] = getOpcodeRep((char)0, (char)1, 164);
        {int[] a = {165,166}; op[164] = getOpcodeCat(a);}
        op[165] = getOpcodeRnm(30, 235); // h16
        {char[] a = {58}; op[166] = getOpcodeTls(a);}
        op[167] = getOpcodeRnm(30, 235); // h16
        {char[] a = {58,58}; op[168] = getOpcodeTls(a);}
        op[169] = getOpcodeRep((char)3, (char)3, 170);
        {int[] a = {171,172}; op[170] = getOpcodeCat(a);}
        op[171] = getOpcodeRnm(30, 235); // h16
        {char[] a = {58}; op[172] = getOpcodeTls(a);}
        op[173] = getOpcodeRnm(29, 229); // ls32
        {int[] a = {175,182,183,187}; op[174] = getOpcodeCat(a);}
        op[175] = getOpcodeRep((char)0, (char)1, 176);
        {int[] a = {177,181}; op[176] = getOpcodeCat(a);}
        op[177] = getOpcodeRep((char)0, (char)2, 178);
        {int[] a = {179,180}; op[178] = getOpcodeCat(a);}
        op[179] = getOpcodeRnm(30, 235); // h16
        {char[] a = {58}; op[180] = getOpcodeTls(a);}
        op[181] = getOpcodeRnm(30, 235); // h16
        {char[] a = {58,58}; op[182] = getOpcodeTls(a);}
        op[183] = getOpcodeRep((char)2, (char)2, 184);
        {int[] a = {185,186}; op[184] = getOpcodeCat(a);}
        op[185] = getOpcodeRnm(30, 235); // h16
        {char[] a = {58}; op[186] = getOpcodeTls(a);}
        op[187] = getOpcodeRnm(29, 229); // ls32
        {int[] a = {189,196,197,198,199}; op[188] = getOpcodeCat(a);}
        op[189] = getOpcodeRep((char)0, (char)1, 190);
        {int[] a = {191,195}; op[190] = getOpcodeCat(a);}
        op[191] = getOpcodeRep((char)0, (char)3, 192);
        {int[] a = {193,194}; op[192] = getOpcodeCat(a);}
        op[193] = getOpcodeRnm(30, 235); // h16
        {char[] a = {58}; op[194] = getOpcodeTls(a);}
        op[195] = getOpcodeRnm(30, 235); // h16
        {char[] a = {58,58}; op[196] = getOpcodeTls(a);}
        op[197] = getOpcodeRnm(30, 235); // h16
        {char[] a = {58}; op[198] = getOpcodeTls(a);}
        op[199] = getOpcodeRnm(29, 229); // ls32
        {int[] a = {201,208,209}; op[200] = getOpcodeCat(a);}
        op[201] = getOpcodeRep((char)0, (char)1, 202);
        {int[] a = {203,207}; op[202] = getOpcodeCat(a);}
        op[203] = getOpcodeRep((char)0, (char)4, 204);
        {int[] a = {205,206}; op[204] = getOpcodeCat(a);}
        op[205] = getOpcodeRnm(30, 235); // h16
        {char[] a = {58}; op[206] = getOpcodeTls(a);}
        op[207] = getOpcodeRnm(30, 235); // h16
        {char[] a = {58,58}; op[208] = getOpcodeTls(a);}
        op[209] = getOpcodeRnm(29, 229); // ls32
        {int[] a = {211,218,219}; op[210] = getOpcodeCat(a);}
        op[211] = getOpcodeRep((char)0, (char)1, 212);
        {int[] a = {213,217}; op[212] = getOpcodeCat(a);}
        op[213] = getOpcodeRep((char)0, (char)5, 214);
        {int[] a = {215,216}; op[214] = getOpcodeCat(a);}
        op[215] = getOpcodeRnm(30, 235); // h16
        {char[] a = {58}; op[216] = getOpcodeTls(a);}
        op[217] = getOpcodeRnm(30, 235); // h16
        {char[] a = {58,58}; op[218] = getOpcodeTls(a);}
        op[219] = getOpcodeRnm(30, 235); // h16
        {int[] a = {221,228}; op[220] = getOpcodeCat(a);}
        op[221] = getOpcodeRep((char)0, (char)1, 222);
        {int[] a = {223,227}; op[222] = getOpcodeCat(a);}
        op[223] = getOpcodeRep((char)0, (char)6, 224);
        {int[] a = {225,226}; op[224] = getOpcodeCat(a);}
        op[225] = getOpcodeRnm(30, 235); // h16
        {char[] a = {58}; op[226] = getOpcodeTls(a);}
        op[227] = getOpcodeRnm(30, 235); // h16
        {char[] a = {58,58}; op[228] = getOpcodeTls(a);}
        {int[] a = {230,234}; op[229] = getOpcodeAlt(a);}
        {int[] a = {231,232,233}; op[230] = getOpcodeCat(a);}
        op[231] = getOpcodeRnm(30, 235); // h16
        {char[] a = {58}; op[232] = getOpcodeTls(a);}
        op[233] = getOpcodeRnm(30, 235); // h16
        op[234] = getOpcodeRnm(31, 237); // IPv4address
        op[235] = getOpcodeRep((char)1, (char)4, 236);
        op[236] = getOpcodeRnm(60, 370); // HEXDIG
        {int[] a = {238,239,240,241,242,243,244}; op[237] = getOpcodeCat(a);}
        op[238] = getOpcodeRnm(32, 245); // dec-octet
        {char[] a = {46}; op[239] = getOpcodeTls(a);}
        op[240] = getOpcodeRnm(32, 245); // dec-octet
        {char[] a = {46}; op[241] = getOpcodeTls(a);}
        op[242] = getOpcodeRnm(32, 245); // dec-octet
        {char[] a = {46}; op[243] = getOpcodeTls(a);}
        op[244] = getOpcodeRnm(32, 245); // dec-octet
        {int[] a = {246,247,250,254,258}; op[245] = getOpcodeAlt(a);}
        op[246] = getOpcodeRnm(58, 368); // DIGIT
        {int[] a = {248,249}; op[247] = getOpcodeCat(a);}
        op[248] = getOpcodeTrg((char)49, (char)57);
        op[249] = getOpcodeRnm(58, 368); // DIGIT
        {int[] a = {251,252}; op[250] = getOpcodeCat(a);}
        {char[] a = {49}; op[251] = getOpcodeTls(a);}
        op[252] = getOpcodeRep((char)2, (char)2, 253);
        op[253] = getOpcodeRnm(58, 368); // DIGIT
        {int[] a = {255,256,257}; op[254] = getOpcodeCat(a);}
        {char[] a = {50}; op[255] = getOpcodeTls(a);}
        op[256] = getOpcodeTrg((char)48, (char)52);
        op[257] = getOpcodeRnm(58, 368); // DIGIT
        {int[] a = {259,260}; op[258] = getOpcodeCat(a);}
        {char[] a = {50,53}; op[259] = getOpcodeTls(a);}
        op[260] = getOpcodeTrg((char)48, (char)53);
        op[261] = getOpcodeRep((char)0, Character.MAX_VALUE, 262);
        {int[] a = {263,264,265}; op[262] = getOpcodeAlt(a);}
        op[263] = getOpcodeRnm(45, 310); // iunreserved
        op[264] = getOpcodeRnm(46, 318); // pct-encoded
        op[265] = getOpcodeRnm(50, 337); // sub-delims
        op[266] = getOpcodeRep((char)0, Character.MAX_VALUE, 267);
        op[267] = getOpcodeRnm(58, 368); // DIGIT
        op[268] = getOpcodeRep((char)0, Character.MAX_VALUE, 269);
        {int[] a = {270,271}; op[269] = getOpcodeCat(a);}
        {char[] a = {47}; op[270] = getOpcodeTls(a);}
        op[271] = getOpcodeRnm(39, 288); // isegment
        {int[] a = {273,274}; op[272] = getOpcodeCat(a);}
        {char[] a = {47}; op[273] = getOpcodeTls(a);}
        op[274] = getOpcodeRep((char)0, (char)1, 275);
        {int[] a = {276,277}; op[275] = getOpcodeCat(a);}
        op[276] = getOpcodeRnm(40, 290); // isegment-nz
        op[277] = getOpcodeRep((char)0, Character.MAX_VALUE, 278);
        {int[] a = {279,280}; op[278] = getOpcodeCat(a);}
        {char[] a = {47}; op[279] = getOpcodeTls(a);}
        op[280] = getOpcodeRnm(39, 288); // isegment
        {int[] a = {282,283}; op[281] = getOpcodeCat(a);}
        op[282] = getOpcodeRnm(40, 290); // isegment-nz
        op[283] = getOpcodeRep((char)0, Character.MAX_VALUE, 284);
        {int[] a = {285,286}; op[284] = getOpcodeCat(a);}
        {char[] a = {47}; op[285] = getOpcodeTls(a);}
        op[286] = getOpcodeRnm(39, 288); // isegment
        {char[] a = {}; op[287] = getOpcodeTls(a);}
        op[288] = getOpcodeRep((char)0, Character.MAX_VALUE, 289);
        op[289] = getOpcodeRnm(44, 304); // ipchar
        op[290] = getOpcodeRep((char)1, Character.MAX_VALUE, 291);
        op[291] = getOpcodeRnm(44, 304); // ipchar
        op[292] = getOpcodeRep((char)0, Character.MAX_VALUE, 293);
        {int[] a = {294,295,296,297}; op[293] = getOpcodeAlt(a);}
        op[294] = getOpcodeRnm(44, 304); // ipchar
        op[295] = getOpcodeRnm(42, 298); // iprivate
        {char[] a = {47}; op[296] = getOpcodeTls(a);}
        {char[] a = {63}; op[297] = getOpcodeTls(a);}
        op[298] = getOpcodeTrg((char)57344, (char)63743);
        op[299] = getOpcodeRep((char)0, Character.MAX_VALUE, 300);
        {int[] a = {301,302,303}; op[300] = getOpcodeAlt(a);}
        op[301] = getOpcodeRnm(44, 304); // ipchar
        {char[] a = {47}; op[302] = getOpcodeTls(a);}
        {char[] a = {63}; op[303] = getOpcodeTls(a);}
        {int[] a = {305,306,307,308,309}; op[304] = getOpcodeAlt(a);}
        op[305] = getOpcodeRnm(45, 310); // iunreserved
        op[306] = getOpcodeRnm(46, 318); // pct-encoded
        op[307] = getOpcodeRnm(50, 337); // sub-delims
        {char[] a = {58}; op[308] = getOpcodeTls(a);}
        {char[] a = {64}; op[309] = getOpcodeTls(a);}
        {int[] a = {311,312,313,314,315,316,317}; op[310] = getOpcodeAlt(a);}
        op[311] = getOpcodeRnm(52, 354); // ALPHA
        op[312] = getOpcodeRnm(58, 368); // DIGIT
        {char[] a = {45}; op[313] = getOpcodeTls(a);}
        {char[] a = {46}; op[314] = getOpcodeTls(a);}
        {char[] a = {95}; op[315] = getOpcodeTls(a);}
        {char[] a = {126}; op[316] = getOpcodeTls(a);}
        op[317] = getOpcodeRnm(47, 322); // ucschar
        {int[] a = {319,320,321}; op[318] = getOpcodeCat(a);}
        {char[] a = {37}; op[319] = getOpcodeTls(a);}
        op[320] = getOpcodeRnm(60, 370); // HEXDIG
        op[321] = getOpcodeRnm(60, 370); // HEXDIG
        {int[] a = {323,324,325}; op[322] = getOpcodeAlt(a);}
        op[323] = getOpcodeTrg((char)160, (char)55295);
        op[324] = getOpcodeTrg((char)63744, (char)64975);
        op[325] = getOpcodeTrg((char)65008, (char)65519);
        {int[] a = {327,328}; op[326] = getOpcodeAlt(a);}
        op[327] = getOpcodeRnm(49, 329); // gen-delims
        op[328] = getOpcodeRnm(50, 337); // sub-delims
        {int[] a = {330,331,332,333,334,335,336}; op[329] = getOpcodeAlt(a);}
        {char[] a = {58}; op[330] = getOpcodeTls(a);}
        {char[] a = {47}; op[331] = getOpcodeTls(a);}
        {char[] a = {63}; op[332] = getOpcodeTls(a);}
        {char[] a = {35}; op[333] = getOpcodeTls(a);}
        {char[] a = {91}; op[334] = getOpcodeTls(a);}
        {char[] a = {93}; op[335] = getOpcodeTls(a);}
        {char[] a = {64}; op[336] = getOpcodeTls(a);}
        {int[] a = {338,339,340,341,342,343,344,345,346}; op[337] = getOpcodeAlt(a);}
        {char[] a = {33}; op[338] = getOpcodeTls(a);}
        {char[] a = {36}; op[339] = getOpcodeTls(a);}
        {char[] a = {38}; op[340] = getOpcodeTls(a);}
        {char[] a = {39}; op[341] = getOpcodeTls(a);}
        {char[] a = {42}; op[342] = getOpcodeTls(a);}
        {char[] a = {43}; op[343] = getOpcodeTls(a);}
        {char[] a = {44}; op[344] = getOpcodeTls(a);}
        {char[] a = {59}; op[345] = getOpcodeTls(a);}
        {char[] a = {61}; op[346] = getOpcodeTls(a);}
        {int[] a = {348,349,350,351,352,353}; op[347] = getOpcodeAlt(a);}
        op[348] = getOpcodeRnm(52, 354); // ALPHA
        op[349] = getOpcodeRnm(58, 368); // DIGIT
        {char[] a = {45}; op[350] = getOpcodeTls(a);}
        {char[] a = {46}; op[351] = getOpcodeTls(a);}
        {char[] a = {95}; op[352] = getOpcodeTls(a);}
        {char[] a = {126}; op[353] = getOpcodeTls(a);}
        {int[] a = {355,356}; op[354] = getOpcodeAlt(a);}
        op[355] = getOpcodeTrg((char)65, (char)90);
        op[356] = getOpcodeTrg((char)97, (char)122);
        {int[] a = {358,359}; op[357] = getOpcodeAlt(a);}
        {char[] a = {48}; op[358] = getOpcodeTls(a);}
        {char[] a = {49}; op[359] = getOpcodeTls(a);}
        op[360] = getOpcodeTrg((char)1, (char)127);
        {char[] a = {13}; op[361] = getOpcodeTbs(a);}
        {int[] a = {363,364}; op[362] = getOpcodeCat(a);}
        op[363] = getOpcodeRnm(55, 361); // CR
        op[364] = getOpcodeRnm(62, 379); // LF
        {int[] a = {366,367}; op[365] = getOpcodeAlt(a);}
        op[366] = getOpcodeTrg((char)0, (char)31);
        {char[] a = {127}; op[367] = getOpcodeTbs(a);}
        op[368] = getOpcodeTrg((char)48, (char)57);
        {char[] a = {34}; op[369] = getOpcodeTbs(a);}
        {int[] a = {371,372,373,374,375,376,377}; op[370] = getOpcodeAlt(a);}
        op[371] = getOpcodeRnm(58, 368); // DIGIT
        {char[] a = {65}; op[372] = getOpcodeTls(a);}
        {char[] a = {66}; op[373] = getOpcodeTls(a);}
        {char[] a = {67}; op[374] = getOpcodeTls(a);}
        {char[] a = {68}; op[375] = getOpcodeTls(a);}
        {char[] a = {69}; op[376] = getOpcodeTls(a);}
        {char[] a = {70}; op[377] = getOpcodeTls(a);}
        {char[] a = {9}; op[378] = getOpcodeTbs(a);}
        {char[] a = {10}; op[379] = getOpcodeTbs(a);}
        op[380] = getOpcodeRep((char)0, Character.MAX_VALUE, 381);
        {int[] a = {382,383}; op[381] = getOpcodeAlt(a);}
        op[382] = getOpcodeRnm(67, 389); // WSP
        {int[] a = {384,385}; op[383] = getOpcodeCat(a);}
        op[384] = getOpcodeRnm(56, 362); // CRLF
        op[385] = getOpcodeRnm(67, 389); // WSP
        op[386] = getOpcodeTrg((char)0, (char)255);
        {char[] a = {32}; op[387] = getOpcodeTbs(a);}
        op[388] = getOpcodeTrg((char)33, (char)126);
        {int[] a = {390,391}; op[389] = getOpcodeAlt(a);}
        op[390] = getOpcodeRnm(65, 387); // SP
        op[391] = getOpcodeRnm(61, 378); // HTAB
        return op;
    }

    public static void display(PrintStream out){
        out.println(";");
        out.println("; xdi2.core.xri3.parser.apg.XDI3Grammar");
        out.println(";");
        out.println(";");
        out.println("; This file contains ABNF rules from the following sources:");
        out.println("; http://wiki.oasis-open.org/xri/XriThree/SyntaxAbnf");
        out.println("; http://wiki.oasis-open.org/xdi/XdiAbnf");
        out.println(";");
        out.println("; A few modifications have been made to the rules:");
        out.println(";");
        out.println("; 1) APG doesn't support prose-val. the \"ipath-empty\" rule has been adjusted accordingly.");
        out.println("; 2) Java regular expressions only support the BMP of unicode. The \"ucschar\" and \"iprivate\"");
        out.println(";    rules have been adjusted accordingly.");
        out.println("; 3) The \"(\" and \")\" characters have been removed from the sub-delims rule to avoid a conflict with the xref rule.");
        out.println(";");
        out.println("; =markus 01-25-2013");
        out.println(";");
        out.println("");
        out.println(";");
        out.println("; XDI rules");
        out.println(";");
        out.println("");
        out.println("xdi-context = xdi-segment");
        out.println("xdi-statement = xdi-subject \"/\" xdi-predicate \"/\" xdi-object");
        out.println("xdi-subject = xdi-segment");
        out.println("xdi-predicate = xdi-segment");
        out.println("xdi-object = xdi-segment");
        out.println("xdi-segment = 1*subseg");
        out.println("subseg = global-subseg / local-subseg / xref");
        out.println("global-subseg = gcs-char [ local-subseg / xref / literal ]");
        out.println("local-subseg = lcs-char [ xref / literal ]");
        out.println("gcs-char          = \"=\" / \"@\" / \"+\" / \"$\"");
        out.println("lcs-char          = \"*\" / \"!\"");
        out.println("xref = xref-empty / xref-IRI / xref-segment / xref-subject-predicate / xref-statement / xref-literal");
        out.println("xref-empty = \"()\"");
        out.println("xref-IRI = \"(\" IRI \")\"");
        out.println("xref-segment = \"(\" xdi-segment \")\"");
        out.println("xref-subject-predicate = \"(\" xdi-subject \"/\" xdi-predicate \")\"");
        out.println("xref-statement = \"(\" xdi-statement \")\"");
        out.println("xref-literal = \"(\" literal \")\"");
        out.println("literal           = 1*xdi-pchar");
        out.println("xdi-pchar         = iunreserved / pct-encoded");
        out.println("");
        out.println(";");
        out.println("; IRI rules");
        out.println(";");
        out.println("");
        out.println("IRI               = scheme \":\" ihier-part [ \"?\" iquery ]");
        out.println("                  [ \"#\" ifragment ]");
        out.println("scheme            = ALPHA *( ALPHA / DIGIT / \"+\" / \"-\" / \".\" )");
        out.println("ihier-part        = \"//\" iauthority ipath-abempty");
        out.println("                  / ipath-abs");
        out.println("                  / ipath-rootless");
        out.println("                  / ipath-empty");
        out.println("iauthority        = [ iuserinfo \"@\" ] ihost [ \":\" port ]");
        out.println("iuserinfo         = *( iunreserved / pct-encoded / sub-delims / \":\" )");
        out.println("ihost             = IP-literal / IPv4address / ireg-name");
        out.println("IP-literal        = \"[\" ( IPv6address / IPvFuture  ) \"]\"");
        out.println("IPvFuture         = \"v\" 1*HEXDIG \".\" 1*( unreserved / sub-delims / \":\" )");
        out.println("IPv6address       =                            6( h16 \":\" ) ls32");
        out.println("                  /                       \"::\" 5( h16 \":\" ) ls32");
        out.println("                  / [               h16 ] \"::\" 4( h16 \":\" ) ls32");
        out.println("                  / [ *1( h16 \":\" ) h16 ] \"::\" 3( h16 \":\" ) ls32");
        out.println("                  / [ *2( h16 \":\" ) h16 ] \"::\" 2( h16 \":\" ) ls32");
        out.println("                  / [ *3( h16 \":\" ) h16 ] \"::\"    h16 \":\"   ls32");
        out.println("                  / [ *4( h16 \":\" ) h16 ] \"::\"              ls32");
        out.println("                  / [ *5( h16 \":\" ) h16 ] \"::\"              h16");
        out.println("                  / [ *6( h16 \":\" ) h16 ] \"::\"");
        out.println("ls32              = ( h16 \":\" h16 ) / IPv4address");
        out.println("h16               = 1*4HEXDIG");
        out.println("IPv4address       = dec-octet \".\" dec-octet \".\" dec-octet \".\" dec-octet");
        out.println("dec-octet         = DIGIT                ; 0-9");
        out.println("                  / %x31-39 DIGIT        ; 10-99");
        out.println("                  / \"1\" 2DIGIT           ; 100-199");
        out.println("                  / \"2\" %x30-34 DIGIT    ; 200-249");
        out.println("                  / \"25\" %x30-35         ; 250-255");
        out.println("ireg-name         = *( iunreserved / pct-encoded / sub-delims )");
        out.println("port              = *DIGIT");
        out.println("ipath-abempty     = *( \"/\" isegment )");
        out.println("ipath-abs         = \"/\" [ isegment-nz *( \"/\" isegment ) ]");
        out.println("ipath-rootless    = isegment-nz *( \"/\" isegment )");
        out.println("ipath-empty       = \"\"");
        out.println("isegment          = *ipchar");
        out.println("isegment-nz       = 1*ipchar");
        out.println("iquery            = *( ipchar / iprivate / \"/\" / \"?\" )");
        out.println("iprivate          = %xE000-F8FF");
        out.println("ifragment         = *( ipchar / \"/\" / \"?\" )");
        out.println("ipchar            = iunreserved / pct-encoded / sub-delims / \":\" / \"@\"");
        out.println("iunreserved       = ALPHA / DIGIT / \"-\" / \".\" / \"_\" / \"~\" / ucschar");
        out.println("pct-encoded       = \"%\" HEXDIG HEXDIG");
        out.println("ucschar           = %xA0-D7FF / %xF900-FDCF / %xFDF0-FFEF");
        out.println("reserved          = gen-delims / sub-delims");
        out.println("gen-delims        = \":\" / \"/\" / \"?\" / \"#\" / \"[\" / \"]\" / \"@\"");
        out.println("sub-delims        = \"!\" / \"$\" / \"&\" / \"'\"");
        out.println("                  / \"*\" / \"+\" / \",\" / \";\" / \"=\"");
        out.println("unreserved        = ALPHA / DIGIT / \"-\" / \".\" / \"_\" / \"~\"");
        out.println("");
        out.println(";");
        out.println("; ABNF core rules");
        out.println(";");
        out.println("");
        out.println("ALPHA          =  %x41-5A / %x61-7A 		; A-Z / a-z");
        out.println("BIT            =  \"0\" / \"1\"");
        out.println("CHAR           =  %x01-7F 				; any 7-bit US-ASCII character, excluding NUL");
        out.println("CR             =  %x0D					; carriage return");
        out.println("CRLF           =  CR LF					; Internet standard newline");
        out.println("CTL            =  %x00-1F / %x7F			; controls");
        out.println("DIGIT          =  %x30-39 				; 0-9");
        out.println("DQUOTE         =  %x22					; \" (Double Quote)");
        out.println("HEXDIG         =  DIGIT / \"A\" / \"B\" / \"C\" / \"D\" / \"E\" / \"F\"");
        out.println("HTAB           =  %x09					; horizontal tab");
        out.println("LF             =  %x0A					; linefeed");
        out.println("LWSP           =  *(WSP / CRLF WSP)		; linear white space (past newline)");
        out.println("OCTET          =  %x00-FF					; 8 bits of data");
        out.println("SP             =  %x20					; space");
        out.println("VCHAR          =  %x21-7E					; visible (printing) characters");
        out.println("WSP            =  SP / HTAB				; white space");
    }
}
