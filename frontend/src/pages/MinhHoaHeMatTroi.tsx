import matTroi from '../../../assets/3d/solar-system/sun/be-mat.webp?no-inline'
import traiDat from '../../../assets/3d/solar-system/earth/be-mat.webp?no-inline'
import saoTho from '../../../assets/3d/solar-system/saturn/be-mat.webp?no-inline'
import saoHoa from '../../../assets/3d/solar-system/mars/be-mat.webp?no-inline'

export default function MinhHoaHeMatTroi() {
  return <div className="tai-khoan-minh-hoa" aria-hidden="true">
    <div className="tk-he-mat-troi">
      <span className="tk-quy-dao tk-quy-dao-trong" />
      <span className="tk-quy-dao tk-quy-dao-giua" />
      <span className="tk-quy-dao tk-quy-dao-ngoai" />
      <img className="tk-thien-the tk-mat-troi" src={matTroi} alt="" />
      <img className="tk-thien-the tk-trai-dat" src={traiDat} alt="" />
      <span className="tk-sao-tho"><img className="tk-thien-the" src={saoTho} alt="" /></span>
      <img className="tk-thien-the tk-sao-hoa" src={saoHoa} alt="" />
      <span className="tk-sao tk-sao-mot" />
      <span className="tk-sao tk-sao-hai" />
      <span className="tk-sao tk-sao-ba" />
    </div>
  </div>
}
