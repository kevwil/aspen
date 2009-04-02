package com.github.kevwil.aspen
/**
 * @author kevinw
 * @since Apr 2, 2009
 */

class ServerConfig
{
  private var h:String = "0.0.0.0"
  private var p:Int = 9110

  def host = h
  def host_= (newhost:String) = h = newhost

  def port = p
  def port_= (newport:Int) = p = newport
}