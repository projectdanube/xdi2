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
    public static int ruleCount = 74;
    public enum RuleNames{
        ALPHA("ALPHA", 58, 387, 3),
        BIT("BIT", 59, 390, 3),
        CHAR("CHAR", 60, 393, 1),
        CR("CR", 61, 394, 1),
        CRLF("CRLF", 62, 395, 3),
        CTL("CTL", 63, 398, 3),
        DEC_OCTET("dec-octet", 38, 278, 16),
        DIGIT("DIGIT", 64, 401, 1),
        DQUOTE("DQUOTE", 65, 402, 1),
        GCS_CHAR("gcs-char", 11, 39, 5),
        GEN_DELIMS("gen-delims", 55, 362, 8),
        GLOBAL_SUBSEG("global-subseg", 9, 26, 7),
        H16("h16", 36, 268, 2),
        HEXDIG("HEXDIG", 66, 403, 8),
        HTAB("HTAB", 67, 411, 1),
        IAUTHORITY("iauthority", 29, 134, 10),
        IFRAGMENT("ifragment", 49, 332, 5),
        IHIER_PART("ihier-part", 28, 126, 8),
        IHOST("ihost", 31, 150, 4),
        IP_LITERAL("IP-literal", 32, 154, 6),
        IPATH_ABEMPTY("ipath-abempty", 41, 301, 4),
        IPATH_ABS("ipath-abs", 42, 305, 9),
        IPATH_EMPTY("ipath-empty", 44, 320, 1),
        IPATH_ROOTLESS("ipath-rootless", 43, 314, 6),
        IPCHAR("ipchar", 50, 337, 6),
        IPRIVATE("iprivate", 48, 331, 1),
        IPV4ADDRESS("IPv4address", 37, 270, 8),
        IPV6ADDRESS("IPv6address", 34, 170, 92),
        IPVFUTURE("IPvFuture", 33, 160, 10),
        IQUERY("iquery", 47, 325, 6),
        IREG_NAME("ireg-name", 39, 294, 5),
        IRI("IRI", 26, 105, 12),
        ISEGMENT("isegment", 45, 321, 2),
        ISEGMENT_NZ("isegment-nz", 46, 323, 2),
        IUNRESERVED("iunreserved", 51, 343, 8),
        IUSERINFO("iuserinfo", 30, 144, 6),
        LCS_CHAR("lcs-char", 12, 44, 3),
        LF("LF", 68, 412, 1),
        LITERAL("literal", 20, 75, 2),
        LOCAL_SUBSEG("local-subseg", 10, 33, 6),
        LS32("ls32", 35, 262, 6),
        LWSP("LWSP", 69, 413, 6),
        OCTET("OCTET", 70, 419, 1),
        PCT_ENCODED("pct-encoded", 52, 351, 4),
        PORT("port", 40, 299, 2),
        RESERVED("reserved", 54, 359, 3),
        SCHEME("scheme", 27, 117, 9),
        SP("SP", 71, 420, 1),
        SUB_DELIMS("sub-delims", 56, 370, 10),
        SUBSEG("subseg", 8, 22, 4),
        UCSCHAR("ucschar", 53, 355, 4),
        UNRESERVED("unreserved", 57, 380, 7),
        VCHAR("VCHAR", 72, 421, 1),
        WSP("WSP", 73, 422, 3),
        XDI_ADDRESS("xdi-address", 0, 0, 4),
        XDI_CONTEXT("xdi-context", 1, 4, 1),
        XDI_GEN_DELIMS("xdi-gen-delims", 24, 89, 11),
        XDI_INNER_GRAPH("xdi-inner-graph", 2, 5, 4),
        XDI_OBJECT("xdi-object", 6, 17, 1),
        XDI_PCHAR("xdi-pchar", 21, 77, 5),
        XDI_PCHAR_NC("xdi-pchar-nc", 22, 82, 4),
        XDI_PREDICATE("xdi-predicate", 5, 16, 1),
        XDI_RESERVED("xdi-reserved", 23, 86, 3),
        XDI_SEGMENT("xdi-segment", 7, 18, 4),
        XDI_STATEMENT("xdi-statement", 3, 9, 6),
        XDI_SUB_DELIMS("xdi-sub-delims", 25, 100, 5),
        XDI_SUBJECT("xdi-subject", 4, 15, 1),
        XREF("xref", 13, 47, 7),
        XREF_CONTEXT("xref-context", 16, 59, 4),
        XREF_EMPTY("xref-empty", 14, 54, 1),
        XREF_INNER_GRAPH("xref-inner-graph", 17, 63, 4),
        XREF_IRI("xref-IRI", 15, 55, 4),
        XREF_LITERAL("xref-literal", 19, 71, 4),
        XREF_STATEMENT("xref-statement", 18, 67, 4);
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
    	Rule[] rules = new Rule[74];
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
    	Opcode[] op = new Opcode[425];
        {int[] a = {1,2,3}; op[0] = getOpcodeAlt(a);}
        op[1] = getOpcodeRnm(1, 4); // xdi-context
        op[2] = getOpcodeRnm(2, 5); // xdi-inner-graph
        op[3] = getOpcodeRnm(3, 9); // xdi-statement
        op[4] = getOpcodeRnm(4, 15); // xdi-subject
        {int[] a = {6,7,8}; op[5] = getOpcodeCat(a);}
        op[6] = getOpcodeRnm(4, 15); // xdi-subject
        {char[] a = {47}; op[7] = getOpcodeTls(a);}
        op[8] = getOpcodeRnm(5, 16); // xdi-predicate
        {int[] a = {10,11,12,13,14}; op[9] = getOpcodeCat(a);}
        op[10] = getOpcodeRnm(4, 15); // xdi-subject
        {char[] a = {47}; op[11] = getOpcodeTls(a);}
        op[12] = getOpcodeRnm(5, 16); // xdi-predicate
        {char[] a = {47}; op[13] = getOpcodeTls(a);}
        op[14] = getOpcodeRnm(6, 17); // xdi-object
        op[15] = getOpcodeRnm(7, 18); // xdi-segment
        op[16] = getOpcodeRnm(7, 18); // xdi-segment
        op[17] = getOpcodeRnm(7, 18); // xdi-segment
        {int[] a = {19,20}; op[18] = getOpcodeCat(a);}
        op[19] = getOpcodeRnm(8, 22); // subseg
        op[20] = getOpcodeRep((char)0, Character.MAX_VALUE, 21);
        op[21] = getOpcodeRnm(8, 22); // subseg
        {int[] a = {23,24,25}; op[22] = getOpcodeAlt(a);}
        op[23] = getOpcodeRnm(9, 26); // global-subseg
        op[24] = getOpcodeRnm(10, 33); // local-subseg
        op[25] = getOpcodeRnm(13, 47); // xref
        {int[] a = {27,28}; op[26] = getOpcodeCat(a);}
        op[27] = getOpcodeRnm(11, 39); // gcs-char
        op[28] = getOpcodeRep((char)0, (char)1, 29);
        {int[] a = {30,31,32}; op[29] = getOpcodeAlt(a);}
        op[30] = getOpcodeRnm(10, 33); // local-subseg
        op[31] = getOpcodeRnm(13, 47); // xref
        op[32] = getOpcodeRnm(20, 75); // literal
        {int[] a = {34,35}; op[33] = getOpcodeCat(a);}
        op[34] = getOpcodeRnm(12, 44); // lcs-char
        op[35] = getOpcodeRep((char)0, (char)1, 36);
        {int[] a = {37,38}; op[36] = getOpcodeAlt(a);}
        op[37] = getOpcodeRnm(13, 47); // xref
        op[38] = getOpcodeRnm(20, 75); // literal
        {int[] a = {40,41,42,43}; op[39] = getOpcodeAlt(a);}
        {char[] a = {61}; op[40] = getOpcodeTls(a);}
        {char[] a = {64}; op[41] = getOpcodeTls(a);}
        {char[] a = {43}; op[42] = getOpcodeTls(a);}
        {char[] a = {36}; op[43] = getOpcodeTls(a);}
        {int[] a = {45,46}; op[44] = getOpcodeAlt(a);}
        {char[] a = {42}; op[45] = getOpcodeTls(a);}
        {char[] a = {33}; op[46] = getOpcodeTls(a);}
        {int[] a = {48,49,50,51,52,53}; op[47] = getOpcodeAlt(a);}
        op[48] = getOpcodeRnm(14, 54); // xref-empty
        op[49] = getOpcodeRnm(15, 55); // xref-IRI
        op[50] = getOpcodeRnm(16, 59); // xref-context
        op[51] = getOpcodeRnm(17, 63); // xref-inner-graph
        op[52] = getOpcodeRnm(18, 67); // xref-statement
        op[53] = getOpcodeRnm(19, 71); // xref-literal
        {char[] a = {40,41}; op[54] = getOpcodeTls(a);}
        {int[] a = {56,57,58}; op[55] = getOpcodeCat(a);}
        {char[] a = {40}; op[56] = getOpcodeTls(a);}
        op[57] = getOpcodeRnm(26, 105); // IRI
        {char[] a = {41}; op[58] = getOpcodeTls(a);}
        {int[] a = {60,61,62}; op[59] = getOpcodeCat(a);}
        {char[] a = {40}; op[60] = getOpcodeTls(a);}
        op[61] = getOpcodeRnm(1, 4); // xdi-context
        {char[] a = {41}; op[62] = getOpcodeTls(a);}
        {int[] a = {64,65,66}; op[63] = getOpcodeCat(a);}
        {char[] a = {40}; op[64] = getOpcodeTls(a);}
        op[65] = getOpcodeRnm(2, 5); // xdi-inner-graph
        {char[] a = {41}; op[66] = getOpcodeTls(a);}
        {int[] a = {68,69,70}; op[67] = getOpcodeCat(a);}
        {char[] a = {40}; op[68] = getOpcodeTls(a);}
        op[69] = getOpcodeRnm(3, 9); // xdi-statement
        {char[] a = {41}; op[70] = getOpcodeTls(a);}
        {int[] a = {72,73,74}; op[71] = getOpcodeCat(a);}
        {char[] a = {40}; op[72] = getOpcodeTls(a);}
        op[73] = getOpcodeRnm(20, 75); // literal
        {char[] a = {41}; op[74] = getOpcodeTls(a);}
        op[75] = getOpcodeRep((char)1, Character.MAX_VALUE, 76);
        op[76] = getOpcodeRnm(21, 77); // xdi-pchar
        {int[] a = {78,79,80,81}; op[77] = getOpcodeAlt(a);}
        op[78] = getOpcodeRnm(51, 343); // iunreserved
        op[79] = getOpcodeRnm(52, 351); // pct-encoded
        op[80] = getOpcodeRnm(25, 100); // xdi-sub-delims
        {char[] a = {58}; op[81] = getOpcodeTls(a);}
        {int[] a = {83,84,85}; op[82] = getOpcodeAlt(a);}
        op[83] = getOpcodeRnm(51, 343); // iunreserved
        op[84] = getOpcodeRnm(52, 351); // pct-encoded
        op[85] = getOpcodeRnm(25, 100); // xdi-sub-delims
        {int[] a = {87,88}; op[86] = getOpcodeAlt(a);}
        op[87] = getOpcodeRnm(24, 89); // xdi-gen-delims
        op[88] = getOpcodeRnm(25, 100); // xdi-sub-delims
        {int[] a = {90,91,92,93,94,95,96,97,98,99}; op[89] = getOpcodeAlt(a);}
        {char[] a = {58}; op[90] = getOpcodeTls(a);}
        {char[] a = {47}; op[91] = getOpcodeTls(a);}
        {char[] a = {63}; op[92] = getOpcodeTls(a);}
        {char[] a = {35}; op[93] = getOpcodeTls(a);}
        {char[] a = {91}; op[94] = getOpcodeTls(a);}
        {char[] a = {93}; op[95] = getOpcodeTls(a);}
        {char[] a = {40}; op[96] = getOpcodeTls(a);}
        {char[] a = {41}; op[97] = getOpcodeTls(a);}
        op[98] = getOpcodeRnm(11, 39); // gcs-char
        op[99] = getOpcodeRnm(12, 44); // lcs-char
        {int[] a = {101,102,103,104}; op[100] = getOpcodeAlt(a);}
        {char[] a = {38}; op[101] = getOpcodeTls(a);}
        {char[] a = {59}; op[102] = getOpcodeTls(a);}
        {char[] a = {44}; op[103] = getOpcodeTls(a);}
        {char[] a = {39}; op[104] = getOpcodeTls(a);}
        {int[] a = {106,107,108,109,113}; op[105] = getOpcodeCat(a);}
        op[106] = getOpcodeRnm(27, 117); // scheme
        {char[] a = {58}; op[107] = getOpcodeTls(a);}
        op[108] = getOpcodeRnm(28, 126); // ihier-part
        op[109] = getOpcodeRep((char)0, (char)1, 110);
        {int[] a = {111,112}; op[110] = getOpcodeCat(a);}
        {char[] a = {63}; op[111] = getOpcodeTls(a);}
        op[112] = getOpcodeRnm(47, 325); // iquery
        op[113] = getOpcodeRep((char)0, (char)1, 114);
        {int[] a = {115,116}; op[114] = getOpcodeCat(a);}
        {char[] a = {35}; op[115] = getOpcodeTls(a);}
        op[116] = getOpcodeRnm(49, 332); // ifragment
        {int[] a = {118,119}; op[117] = getOpcodeCat(a);}
        op[118] = getOpcodeRnm(58, 387); // ALPHA
        op[119] = getOpcodeRep((char)0, Character.MAX_VALUE, 120);
        {int[] a = {121,122,123,124,125}; op[120] = getOpcodeAlt(a);}
        op[121] = getOpcodeRnm(58, 387); // ALPHA
        op[122] = getOpcodeRnm(64, 401); // DIGIT
        {char[] a = {43}; op[123] = getOpcodeTls(a);}
        {char[] a = {45}; op[124] = getOpcodeTls(a);}
        {char[] a = {46}; op[125] = getOpcodeTls(a);}
        {int[] a = {127,131,132,133}; op[126] = getOpcodeAlt(a);}
        {int[] a = {128,129,130}; op[127] = getOpcodeCat(a);}
        {char[] a = {47,47}; op[128] = getOpcodeTls(a);}
        op[129] = getOpcodeRnm(29, 134); // iauthority
        op[130] = getOpcodeRnm(41, 301); // ipath-abempty
        op[131] = getOpcodeRnm(42, 305); // ipath-abs
        op[132] = getOpcodeRnm(43, 314); // ipath-rootless
        op[133] = getOpcodeRnm(44, 320); // ipath-empty
        {int[] a = {135,139,140}; op[134] = getOpcodeCat(a);}
        op[135] = getOpcodeRep((char)0, (char)1, 136);
        {int[] a = {137,138}; op[136] = getOpcodeCat(a);}
        op[137] = getOpcodeRnm(30, 144); // iuserinfo
        {char[] a = {64}; op[138] = getOpcodeTls(a);}
        op[139] = getOpcodeRnm(31, 150); // ihost
        op[140] = getOpcodeRep((char)0, (char)1, 141);
        {int[] a = {142,143}; op[141] = getOpcodeCat(a);}
        {char[] a = {58}; op[142] = getOpcodeTls(a);}
        op[143] = getOpcodeRnm(40, 299); // port
        op[144] = getOpcodeRep((char)0, Character.MAX_VALUE, 145);
        {int[] a = {146,147,148,149}; op[145] = getOpcodeAlt(a);}
        op[146] = getOpcodeRnm(51, 343); // iunreserved
        op[147] = getOpcodeRnm(52, 351); // pct-encoded
        op[148] = getOpcodeRnm(56, 370); // sub-delims
        {char[] a = {58}; op[149] = getOpcodeTls(a);}
        {int[] a = {151,152,153}; op[150] = getOpcodeAlt(a);}
        op[151] = getOpcodeRnm(32, 154); // IP-literal
        op[152] = getOpcodeRnm(37, 270); // IPv4address
        op[153] = getOpcodeRnm(39, 294); // ireg-name
        {int[] a = {155,156,159}; op[154] = getOpcodeCat(a);}
        {char[] a = {91}; op[155] = getOpcodeTls(a);}
        {int[] a = {157,158}; op[156] = getOpcodeAlt(a);}
        op[157] = getOpcodeRnm(34, 170); // IPv6address
        op[158] = getOpcodeRnm(33, 160); // IPvFuture
        {char[] a = {93}; op[159] = getOpcodeTls(a);}
        {int[] a = {161,162,164,165}; op[160] = getOpcodeCat(a);}
        {char[] a = {118}; op[161] = getOpcodeTls(a);}
        op[162] = getOpcodeRep((char)1, Character.MAX_VALUE, 163);
        op[163] = getOpcodeRnm(66, 403); // HEXDIG
        {char[] a = {46}; op[164] = getOpcodeTls(a);}
        op[165] = getOpcodeRep((char)1, Character.MAX_VALUE, 166);
        {int[] a = {167,168,169}; op[166] = getOpcodeAlt(a);}
        op[167] = getOpcodeRnm(57, 380); // unreserved
        op[168] = getOpcodeRnm(56, 370); // sub-delims
        {char[] a = {58}; op[169] = getOpcodeTls(a);}
        {int[] a = {171,177,184,193,207,221,233,243,253}; op[170] = getOpcodeAlt(a);}
        {int[] a = {172,176}; op[171] = getOpcodeCat(a);}
        op[172] = getOpcodeRep((char)6, (char)6, 173);
        {int[] a = {174,175}; op[173] = getOpcodeCat(a);}
        op[174] = getOpcodeRnm(36, 268); // h16
        {char[] a = {58}; op[175] = getOpcodeTls(a);}
        op[176] = getOpcodeRnm(35, 262); // ls32
        {int[] a = {178,179,183}; op[177] = getOpcodeCat(a);}
        {char[] a = {58,58}; op[178] = getOpcodeTls(a);}
        op[179] = getOpcodeRep((char)5, (char)5, 180);
        {int[] a = {181,182}; op[180] = getOpcodeCat(a);}
        op[181] = getOpcodeRnm(36, 268); // h16
        {char[] a = {58}; op[182] = getOpcodeTls(a);}
        op[183] = getOpcodeRnm(35, 262); // ls32
        {int[] a = {185,187,188,192}; op[184] = getOpcodeCat(a);}
        op[185] = getOpcodeRep((char)0, (char)1, 186);
        op[186] = getOpcodeRnm(36, 268); // h16
        {char[] a = {58,58}; op[187] = getOpcodeTls(a);}
        op[188] = getOpcodeRep((char)4, (char)4, 189);
        {int[] a = {190,191}; op[189] = getOpcodeCat(a);}
        op[190] = getOpcodeRnm(36, 268); // h16
        {char[] a = {58}; op[191] = getOpcodeTls(a);}
        op[192] = getOpcodeRnm(35, 262); // ls32
        {int[] a = {194,201,202,206}; op[193] = getOpcodeCat(a);}
        op[194] = getOpcodeRep((char)0, (char)1, 195);
        {int[] a = {196,200}; op[195] = getOpcodeCat(a);}
        op[196] = getOpcodeRep((char)0, (char)1, 197);
        {int[] a = {198,199}; op[197] = getOpcodeCat(a);}
        op[198] = getOpcodeRnm(36, 268); // h16
        {char[] a = {58}; op[199] = getOpcodeTls(a);}
        op[200] = getOpcodeRnm(36, 268); // h16
        {char[] a = {58,58}; op[201] = getOpcodeTls(a);}
        op[202] = getOpcodeRep((char)3, (char)3, 203);
        {int[] a = {204,205}; op[203] = getOpcodeCat(a);}
        op[204] = getOpcodeRnm(36, 268); // h16
        {char[] a = {58}; op[205] = getOpcodeTls(a);}
        op[206] = getOpcodeRnm(35, 262); // ls32
        {int[] a = {208,215,216,220}; op[207] = getOpcodeCat(a);}
        op[208] = getOpcodeRep((char)0, (char)1, 209);
        {int[] a = {210,214}; op[209] = getOpcodeCat(a);}
        op[210] = getOpcodeRep((char)0, (char)2, 211);
        {int[] a = {212,213}; op[211] = getOpcodeCat(a);}
        op[212] = getOpcodeRnm(36, 268); // h16
        {char[] a = {58}; op[213] = getOpcodeTls(a);}
        op[214] = getOpcodeRnm(36, 268); // h16
        {char[] a = {58,58}; op[215] = getOpcodeTls(a);}
        op[216] = getOpcodeRep((char)2, (char)2, 217);
        {int[] a = {218,219}; op[217] = getOpcodeCat(a);}
        op[218] = getOpcodeRnm(36, 268); // h16
        {char[] a = {58}; op[219] = getOpcodeTls(a);}
        op[220] = getOpcodeRnm(35, 262); // ls32
        {int[] a = {222,229,230,231,232}; op[221] = getOpcodeCat(a);}
        op[222] = getOpcodeRep((char)0, (char)1, 223);
        {int[] a = {224,228}; op[223] = getOpcodeCat(a);}
        op[224] = getOpcodeRep((char)0, (char)3, 225);
        {int[] a = {226,227}; op[225] = getOpcodeCat(a);}
        op[226] = getOpcodeRnm(36, 268); // h16
        {char[] a = {58}; op[227] = getOpcodeTls(a);}
        op[228] = getOpcodeRnm(36, 268); // h16
        {char[] a = {58,58}; op[229] = getOpcodeTls(a);}
        op[230] = getOpcodeRnm(36, 268); // h16
        {char[] a = {58}; op[231] = getOpcodeTls(a);}
        op[232] = getOpcodeRnm(35, 262); // ls32
        {int[] a = {234,241,242}; op[233] = getOpcodeCat(a);}
        op[234] = getOpcodeRep((char)0, (char)1, 235);
        {int[] a = {236,240}; op[235] = getOpcodeCat(a);}
        op[236] = getOpcodeRep((char)0, (char)4, 237);
        {int[] a = {238,239}; op[237] = getOpcodeCat(a);}
        op[238] = getOpcodeRnm(36, 268); // h16
        {char[] a = {58}; op[239] = getOpcodeTls(a);}
        op[240] = getOpcodeRnm(36, 268); // h16
        {char[] a = {58,58}; op[241] = getOpcodeTls(a);}
        op[242] = getOpcodeRnm(35, 262); // ls32
        {int[] a = {244,251,252}; op[243] = getOpcodeCat(a);}
        op[244] = getOpcodeRep((char)0, (char)1, 245);
        {int[] a = {246,250}; op[245] = getOpcodeCat(a);}
        op[246] = getOpcodeRep((char)0, (char)5, 247);
        {int[] a = {248,249}; op[247] = getOpcodeCat(a);}
        op[248] = getOpcodeRnm(36, 268); // h16
        {char[] a = {58}; op[249] = getOpcodeTls(a);}
        op[250] = getOpcodeRnm(36, 268); // h16
        {char[] a = {58,58}; op[251] = getOpcodeTls(a);}
        op[252] = getOpcodeRnm(36, 268); // h16
        {int[] a = {254,261}; op[253] = getOpcodeCat(a);}
        op[254] = getOpcodeRep((char)0, (char)1, 255);
        {int[] a = {256,260}; op[255] = getOpcodeCat(a);}
        op[256] = getOpcodeRep((char)0, (char)6, 257);
        {int[] a = {258,259}; op[257] = getOpcodeCat(a);}
        op[258] = getOpcodeRnm(36, 268); // h16
        {char[] a = {58}; op[259] = getOpcodeTls(a);}
        op[260] = getOpcodeRnm(36, 268); // h16
        {char[] a = {58,58}; op[261] = getOpcodeTls(a);}
        {int[] a = {263,267}; op[262] = getOpcodeAlt(a);}
        {int[] a = {264,265,266}; op[263] = getOpcodeCat(a);}
        op[264] = getOpcodeRnm(36, 268); // h16
        {char[] a = {58}; op[265] = getOpcodeTls(a);}
        op[266] = getOpcodeRnm(36, 268); // h16
        op[267] = getOpcodeRnm(37, 270); // IPv4address
        op[268] = getOpcodeRep((char)1, (char)4, 269);
        op[269] = getOpcodeRnm(66, 403); // HEXDIG
        {int[] a = {271,272,273,274,275,276,277}; op[270] = getOpcodeCat(a);}
        op[271] = getOpcodeRnm(38, 278); // dec-octet
        {char[] a = {46}; op[272] = getOpcodeTls(a);}
        op[273] = getOpcodeRnm(38, 278); // dec-octet
        {char[] a = {46}; op[274] = getOpcodeTls(a);}
        op[275] = getOpcodeRnm(38, 278); // dec-octet
        {char[] a = {46}; op[276] = getOpcodeTls(a);}
        op[277] = getOpcodeRnm(38, 278); // dec-octet
        {int[] a = {279,280,283,287,291}; op[278] = getOpcodeAlt(a);}
        op[279] = getOpcodeRnm(64, 401); // DIGIT
        {int[] a = {281,282}; op[280] = getOpcodeCat(a);}
        op[281] = getOpcodeTrg((char)49, (char)57);
        op[282] = getOpcodeRnm(64, 401); // DIGIT
        {int[] a = {284,285}; op[283] = getOpcodeCat(a);}
        {char[] a = {49}; op[284] = getOpcodeTls(a);}
        op[285] = getOpcodeRep((char)2, (char)2, 286);
        op[286] = getOpcodeRnm(64, 401); // DIGIT
        {int[] a = {288,289,290}; op[287] = getOpcodeCat(a);}
        {char[] a = {50}; op[288] = getOpcodeTls(a);}
        op[289] = getOpcodeTrg((char)48, (char)52);
        op[290] = getOpcodeRnm(64, 401); // DIGIT
        {int[] a = {292,293}; op[291] = getOpcodeCat(a);}
        {char[] a = {50,53}; op[292] = getOpcodeTls(a);}
        op[293] = getOpcodeTrg((char)48, (char)53);
        op[294] = getOpcodeRep((char)0, Character.MAX_VALUE, 295);
        {int[] a = {296,297,298}; op[295] = getOpcodeAlt(a);}
        op[296] = getOpcodeRnm(51, 343); // iunreserved
        op[297] = getOpcodeRnm(52, 351); // pct-encoded
        op[298] = getOpcodeRnm(56, 370); // sub-delims
        op[299] = getOpcodeRep((char)0, Character.MAX_VALUE, 300);
        op[300] = getOpcodeRnm(64, 401); // DIGIT
        op[301] = getOpcodeRep((char)0, Character.MAX_VALUE, 302);
        {int[] a = {303,304}; op[302] = getOpcodeCat(a);}
        {char[] a = {47}; op[303] = getOpcodeTls(a);}
        op[304] = getOpcodeRnm(45, 321); // isegment
        {int[] a = {306,307}; op[305] = getOpcodeCat(a);}
        {char[] a = {47}; op[306] = getOpcodeTls(a);}
        op[307] = getOpcodeRep((char)0, (char)1, 308);
        {int[] a = {309,310}; op[308] = getOpcodeCat(a);}
        op[309] = getOpcodeRnm(46, 323); // isegment-nz
        op[310] = getOpcodeRep((char)0, Character.MAX_VALUE, 311);
        {int[] a = {312,313}; op[311] = getOpcodeCat(a);}
        {char[] a = {47}; op[312] = getOpcodeTls(a);}
        op[313] = getOpcodeRnm(45, 321); // isegment
        {int[] a = {315,316}; op[314] = getOpcodeCat(a);}
        op[315] = getOpcodeRnm(46, 323); // isegment-nz
        op[316] = getOpcodeRep((char)0, Character.MAX_VALUE, 317);
        {int[] a = {318,319}; op[317] = getOpcodeCat(a);}
        {char[] a = {47}; op[318] = getOpcodeTls(a);}
        op[319] = getOpcodeRnm(45, 321); // isegment
        {char[] a = {}; op[320] = getOpcodeTls(a);}
        op[321] = getOpcodeRep((char)0, Character.MAX_VALUE, 322);
        op[322] = getOpcodeRnm(50, 337); // ipchar
        op[323] = getOpcodeRep((char)1, Character.MAX_VALUE, 324);
        op[324] = getOpcodeRnm(50, 337); // ipchar
        op[325] = getOpcodeRep((char)0, Character.MAX_VALUE, 326);
        {int[] a = {327,328,329,330}; op[326] = getOpcodeAlt(a);}
        op[327] = getOpcodeRnm(50, 337); // ipchar
        op[328] = getOpcodeRnm(48, 331); // iprivate
        {char[] a = {47}; op[329] = getOpcodeTls(a);}
        {char[] a = {63}; op[330] = getOpcodeTls(a);}
        op[331] = getOpcodeTrg((char)57344, (char)63743);
        op[332] = getOpcodeRep((char)0, Character.MAX_VALUE, 333);
        {int[] a = {334,335,336}; op[333] = getOpcodeAlt(a);}
        op[334] = getOpcodeRnm(50, 337); // ipchar
        {char[] a = {47}; op[335] = getOpcodeTls(a);}
        {char[] a = {63}; op[336] = getOpcodeTls(a);}
        {int[] a = {338,339,340,341,342}; op[337] = getOpcodeAlt(a);}
        op[338] = getOpcodeRnm(51, 343); // iunreserved
        op[339] = getOpcodeRnm(52, 351); // pct-encoded
        op[340] = getOpcodeRnm(56, 370); // sub-delims
        {char[] a = {58}; op[341] = getOpcodeTls(a);}
        {char[] a = {64}; op[342] = getOpcodeTls(a);}
        {int[] a = {344,345,346,347,348,349,350}; op[343] = getOpcodeAlt(a);}
        op[344] = getOpcodeRnm(58, 387); // ALPHA
        op[345] = getOpcodeRnm(64, 401); // DIGIT
        {char[] a = {45}; op[346] = getOpcodeTls(a);}
        {char[] a = {46}; op[347] = getOpcodeTls(a);}
        {char[] a = {95}; op[348] = getOpcodeTls(a);}
        {char[] a = {126}; op[349] = getOpcodeTls(a);}
        op[350] = getOpcodeRnm(53, 355); // ucschar
        {int[] a = {352,353,354}; op[351] = getOpcodeCat(a);}
        {char[] a = {37}; op[352] = getOpcodeTls(a);}
        op[353] = getOpcodeRnm(66, 403); // HEXDIG
        op[354] = getOpcodeRnm(66, 403); // HEXDIG
        {int[] a = {356,357,358}; op[355] = getOpcodeAlt(a);}
        op[356] = getOpcodeTrg((char)160, (char)55295);
        op[357] = getOpcodeTrg((char)63744, (char)64975);
        op[358] = getOpcodeTrg((char)65008, (char)65519);
        {int[] a = {360,361}; op[359] = getOpcodeAlt(a);}
        op[360] = getOpcodeRnm(55, 362); // gen-delims
        op[361] = getOpcodeRnm(56, 370); // sub-delims
        {int[] a = {363,364,365,366,367,368,369}; op[362] = getOpcodeAlt(a);}
        {char[] a = {58}; op[363] = getOpcodeTls(a);}
        {char[] a = {47}; op[364] = getOpcodeTls(a);}
        {char[] a = {63}; op[365] = getOpcodeTls(a);}
        {char[] a = {35}; op[366] = getOpcodeTls(a);}
        {char[] a = {91}; op[367] = getOpcodeTls(a);}
        {char[] a = {93}; op[368] = getOpcodeTls(a);}
        {char[] a = {64}; op[369] = getOpcodeTls(a);}
        {int[] a = {371,372,373,374,375,376,377,378,379}; op[370] = getOpcodeAlt(a);}
        {char[] a = {33}; op[371] = getOpcodeTls(a);}
        {char[] a = {36}; op[372] = getOpcodeTls(a);}
        {char[] a = {38}; op[373] = getOpcodeTls(a);}
        {char[] a = {39}; op[374] = getOpcodeTls(a);}
        {char[] a = {42}; op[375] = getOpcodeTls(a);}
        {char[] a = {43}; op[376] = getOpcodeTls(a);}
        {char[] a = {44}; op[377] = getOpcodeTls(a);}
        {char[] a = {59}; op[378] = getOpcodeTls(a);}
        {char[] a = {61}; op[379] = getOpcodeTls(a);}
        {int[] a = {381,382,383,384,385,386}; op[380] = getOpcodeAlt(a);}
        op[381] = getOpcodeRnm(58, 387); // ALPHA
        op[382] = getOpcodeRnm(64, 401); // DIGIT
        {char[] a = {45}; op[383] = getOpcodeTls(a);}
        {char[] a = {46}; op[384] = getOpcodeTls(a);}
        {char[] a = {95}; op[385] = getOpcodeTls(a);}
        {char[] a = {126}; op[386] = getOpcodeTls(a);}
        {int[] a = {388,389}; op[387] = getOpcodeAlt(a);}
        op[388] = getOpcodeTrg((char)65, (char)90);
        op[389] = getOpcodeTrg((char)97, (char)122);
        {int[] a = {391,392}; op[390] = getOpcodeAlt(a);}
        {char[] a = {48}; op[391] = getOpcodeTls(a);}
        {char[] a = {49}; op[392] = getOpcodeTls(a);}
        op[393] = getOpcodeTrg((char)1, (char)127);
        {char[] a = {13}; op[394] = getOpcodeTbs(a);}
        {int[] a = {396,397}; op[395] = getOpcodeCat(a);}
        op[396] = getOpcodeRnm(61, 394); // CR
        op[397] = getOpcodeRnm(68, 412); // LF
        {int[] a = {399,400}; op[398] = getOpcodeAlt(a);}
        op[399] = getOpcodeTrg((char)0, (char)31);
        {char[] a = {127}; op[400] = getOpcodeTbs(a);}
        op[401] = getOpcodeTrg((char)48, (char)57);
        {char[] a = {34}; op[402] = getOpcodeTbs(a);}
        {int[] a = {404,405,406,407,408,409,410}; op[403] = getOpcodeAlt(a);}
        op[404] = getOpcodeRnm(64, 401); // DIGIT
        {char[] a = {65}; op[405] = getOpcodeTls(a);}
        {char[] a = {66}; op[406] = getOpcodeTls(a);}
        {char[] a = {67}; op[407] = getOpcodeTls(a);}
        {char[] a = {68}; op[408] = getOpcodeTls(a);}
        {char[] a = {69}; op[409] = getOpcodeTls(a);}
        {char[] a = {70}; op[410] = getOpcodeTls(a);}
        {char[] a = {9}; op[411] = getOpcodeTbs(a);}
        {char[] a = {10}; op[412] = getOpcodeTbs(a);}
        op[413] = getOpcodeRep((char)0, Character.MAX_VALUE, 414);
        {int[] a = {415,416}; op[414] = getOpcodeAlt(a);}
        op[415] = getOpcodeRnm(73, 422); // WSP
        {int[] a = {417,418}; op[416] = getOpcodeCat(a);}
        op[417] = getOpcodeRnm(62, 395); // CRLF
        op[418] = getOpcodeRnm(73, 422); // WSP
        op[419] = getOpcodeTrg((char)0, (char)255);
        {char[] a = {32}; op[420] = getOpcodeTbs(a);}
        op[421] = getOpcodeTrg((char)33, (char)126);
        {int[] a = {423,424}; op[422] = getOpcodeAlt(a);}
        op[423] = getOpcodeRnm(71, 420); // SP
        op[424] = getOpcodeRnm(67, 411); // HTAB
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
        out.println("xdi-address = xdi-context / xdi-inner-graph / xdi-statement");
        out.println("xdi-context = xdi-subject");
        out.println("xdi-inner-graph = xdi-subject \"/\" xdi-predicate");
        out.println("xdi-statement = xdi-subject \"/\" xdi-predicate \"/\" xdi-object");
        out.println("xdi-subject = xdi-segment");
        out.println("xdi-predicate = xdi-segment");
        out.println("xdi-object = xdi-segment");
        out.println("xdi-segment = subseg *subseg");
        out.println("subseg = global-subseg / local-subseg / xref");
        out.println("global-subseg = gcs-char [ local-subseg / xref / literal ]");
        out.println("local-subseg = lcs-char [ xref / literal ]");
        out.println("gcs-char          = \"=\" / \"@\" / \"+\" / \"$\"");
        out.println("lcs-char          = \"*\" / \"!\"");
        out.println("xref = xref-empty / xref-IRI / xref-context / xref-inner-graph / xref-statement / xref-literal");
        out.println("xref-empty = \"()\"");
        out.println("xref-IRI = \"(\" IRI \")\"");
        out.println("xref-context = \"(\" xdi-context \")\"");
        out.println("xref-inner-graph = \"(\" xdi-inner-graph \")\"");
        out.println("xref-statement = \"(\" xdi-statement \")\"");
        out.println("xref-literal = \"(\" literal \")\"");
        out.println("literal           = 1*xdi-pchar");
        out.println("xdi-pchar         = iunreserved / pct-encoded / xdi-sub-delims / \":\"");
        out.println("xdi-pchar-nc      = iunreserved / pct-encoded / xdi-sub-delims");
        out.println("xdi-reserved      = xdi-gen-delims / xdi-sub-delims");
        out.println("xdi-gen-delims    = \":\" / \"/\" / \"?\" / \"#\" / \"[\" / \"]\" / \"(\" / \")\" / gcs-char / lcs-char");
        out.println("xdi-sub-delims    = \"&\" / \";\" / \",\" / \"'\"");
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
