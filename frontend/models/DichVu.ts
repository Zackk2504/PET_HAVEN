export default class  DichVu {
    id: number;
    tendichvu: string;
    mota: string;
    anh: string | null;
    giatien: number;



    constructor(id: number, tendichvu: string, mota: string, anh: string | null, giatien: number) {
        this.id = id;
        this.tendichvu = tendichvu;
        this.mota = mota;
        this.anh = anh;
        this.giatien = giatien;
    }
}
