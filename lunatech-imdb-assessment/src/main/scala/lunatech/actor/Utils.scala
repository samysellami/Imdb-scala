package lunatech.actors

import lunatech.models.Title
import lunatech.models.{InfoTitle, Principals, Crew}

/**
 * A Utility object to aggregate title informations, 
 * querying the database returns rows that might have the same title, 
 * this object allow us to group those duplicates 
 */
object Utils {

  def aggregateData(infos: Seq[InfoTitle]): Seq[InfoTitle] = {
    
    infos.toSeq.groupBy(_.title)
    .map{
      case(k, v) => InfoTitle(
        k, 
        v.foldLeft(List[Principals]())( 
          (a, f) => {
            if (a.contains(f.cast.head)) 
              a
            else 
              a:+ f.cast.head
          }
        ), 
        v.foldLeft(List[Crew]())( 
          (a, f) => {
            if (a.contains(f.crew.head)) 
              a
            else
              a:+ f.crew.head
          }
        ) 
      )
    }.toSeq
  }
}
