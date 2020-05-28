import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { errorRoute, navbarRoute } from './layouts';
import { DEBUG_INFO_ENABLED } from 'app/app.constants';
import { GamesComponent } from './games/games.component';
import { NewGameComponent } from './new-game/new-game.component';
import { PlayerViewComponent } from './player-view/player-view.component';

const LAYOUT_ROUTES = [navbarRoute, ...errorRoute];

@NgModule({
  imports: [
    RouterModule.forRoot(
      [
        {
          path: 'games',
          component: GamesComponent
        },
        {
          path: 'new-game',
          component: NewGameComponent
        },
        {
          path: 'board-view/:gameId/:playerNum',
          component: PlayerViewComponent
        },
        {
          path: 'admin',
          loadChildren: './admin/admin.module#JhipsterPegsAndJokersAdminModule'
        },
        ...LAYOUT_ROUTES
      ],
      { enableTracing: DEBUG_INFO_ENABLED }
    )
  ],
  exports: [RouterModule]
})
export class JhipsterPegsAndJokersAppRoutingModule {}
